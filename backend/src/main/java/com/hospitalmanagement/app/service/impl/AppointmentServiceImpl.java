package com.hospitalmanagement.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospitalmanagement.app.dto.AppointmentRequestDTO;
import com.hospitalmanagement.app.dto.AppointmentResponseDTO;
import com.hospitalmanagement.app.entity.Appointment;
import com.hospitalmanagement.app.entity.AppointmentStatus;
import com.hospitalmanagement.app.entity.AvailableSlot;
import com.hospitalmanagement.app.entity.Role;
import com.hospitalmanagement.app.entity.User;
import com.hospitalmanagement.app.repository.AppointmentRepository;
import com.hospitalmanagement.app.repository.AvailableSlotRepository;
import com.hospitalmanagement.app.repository.UserRepository;
import com.hospitalmanagement.app.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository        userRepository;
    private final AvailableSlotRepository slotRepository;

    //Create 

    @Override
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto, String patientEmail) {

        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + dto.getDoctorId()));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Selected user is not a doctor.");
        }

        // Business Rule 1: Mode Validation
        if (doctor.getMode() != null && !doctor.getMode().equalsIgnoreCase(dto.getMode())) {
            throw new RuntimeException("Doctor only accepts " + doctor.getMode() + " appointments.");
        }

        // Business Rule 2: Slot Locking
        List<AvailableSlot> slots = slotRepository.findByDoctorIdAndDateAndIsAvailable(
            doctor.getId(), dto.getAppointmentDate(), true);
        
        AvailableSlot matchedSlot = slots.stream()
            .filter(s -> s.getStartTime().equals(dto.getStartTime()) && s.getEndTime().equals(dto.getEndTime()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Slot is not available or already locked."));
        
        // Lock the slot
        matchedSlot.setAvailable(false);
        slotRepository.save(matchedSlot);

        // Check overlaps just in case
        List<Appointment> overlaps = appointmentRepository.findOverlappingAppointments(
                doctor,
                dto.getAppointmentDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("This slot is already booked. Please choose a different time.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setFee(0.0);
        appointment.setMode(dto.getMode());

        if ("ONLINE".equalsIgnoreCase(dto.getMode())) {
            appointment.setVideoLink("https://meet.hospital.com/" + UUID.randomUUID().toString().substring(0, 8));
        } else if ("OFFLINE".equalsIgnoreCase(dto.getMode())) {
            appointment.setClinicAddress("123 Main Hospital Road, City Center");
        }

        return toResponse(appointmentRepository.save(appointment));
    }

    //Read

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointments(String email, String role) {

        String upperRole = role.toUpperCase();

        if (upperRole.equals("ADMIN") || upperRole.equals("ROLE_ADMIN")) {
            return appointmentRepository.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Appointment> appointments = switch (upperRole) {
            case "ROLE_PATIENT", "PATIENT" -> appointmentRepository.findByPatient(user);
            case "ROLE_DOCTOR", "DOCTOR"   -> appointmentRepository.findByDoctor(user);
            default -> throw new RuntimeException("Unknown role: " + role);
        };

        return appointments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long id, String callerEmail, String role) {

        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));

        boolean isAdmin   = role.equalsIgnoreCase("ROLE_ADMIN") || role.equalsIgnoreCase("ADMIN");
        boolean isPatient = appt.getPatient().getEmail().equals(callerEmail);
        boolean isDoctor  = appt.getDoctor().getEmail().equals(callerEmail);

        if (!isAdmin && !isPatient && !isDoctor) {
            throw new AccessDeniedException("You do not have permission to view this appointment.");
        }

        return toResponse(appt);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
        appt.setStatus(AppointmentStatus.CANCELLED);
        return toResponse(appointmentRepository.save(appt));
    }

    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, String status) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
        appt.setStatus(AppointmentStatus.valueOf(status.toUpperCase()));
        return toResponse(appointmentRepository.save(appt));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    //Mapper 

    private AppointmentResponseDTO toResponse(Appointment a) {
        return new AppointmentResponseDTO(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getDoctor().getId(),
                a.getDoctor().getName(),
                a.getAppointmentDate(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getFee(),
                a.getMode(),
                a.getVideoLink(),
                a.getClinicAddress(),
                a.getCreatedAt()
        );
    }
}