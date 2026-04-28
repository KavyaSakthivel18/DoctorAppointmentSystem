package com.hospitalmanagement.app.controller;

import com.hospitalmanagement.app.entity.Role;
import com.hospitalmanagement.app.entity.User;
import com.hospitalmanagement.app.repository.UserRepository;
import com.hospitalmanagement.app.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final UserRepository userRepository;
    private final DoctorService doctorService;
    private final com.hospitalmanagement.app.repository.AvailableSlotRepository slotRepository;

    @GetMapping
    public ResponseEntity<List<User>> getDoctors(
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) String mode) {
        
        if (specialtyId != null && mode != null) {
            return ResponseEntity.ok(userRepository.findByRoleAndDepartmentIdAndMode(Role.DOCTOR, specialtyId, mode));
        } else if (specialtyId != null) {
            return ResponseEntity.ok(userRepository.findByRoleAndDepartmentId(Role.DOCTOR, specialtyId));
        } else if (mode != null) {
            return ResponseEntity.ok(userRepository.findByRoleAndMode(Role.DOCTOR, mode));
        }
        return ResponseEntity.ok(userRepository.findByRole(Role.DOCTOR));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getDoctorById(@PathVariable Long id) {
        User doctor = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.DOCTOR)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return ResponseEntity.ok(doctor);
    }

    @PostMapping
    public ResponseEntity<User> addDoctor(@RequestBody User doctor) {
        doctor.setRole(Role.DOCTOR);
        return ResponseEntity.ok(userRepository.save(doctor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateDoctor(@PathVariable Long id, @RequestBody User doctorDetails) {
        User doctor = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.DOCTOR)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        doctor.setName(doctorDetails.getName());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setMode(doctorDetails.getMode());
        if (doctorDetails.getDepartment() != null) {
            doctor.setDepartment(doctorDetails.getDepartment());
        }
        return ResponseEntity.ok(userRepository.save(doctor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/mode")
    public ResponseEntity<Map<String, String>> getDoctorMode(@PathVariable Long id) {
        User doctor = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.DOCTOR)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return ResponseEntity.ok(Map.of("mode", doctor.getMode() != null ? doctor.getMode() : "UNSPECIFIED"));
    }

    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<?> getDoctorSlots(@PathVariable Long doctorId) {
        return ResponseEntity.ok(slotRepository.findByDoctorId(doctorId));
    }
}