package com.hospitalmanagement.app.controller;

import com.hospitalmanagement.app.dto.AppointmentRequestDTO;
import com.hospitalmanagement.app.dto.AppointmentResponseDTO;
import com.hospitalmanagement.app.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    //POST /api/appointments
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @RequestBody AppointmentRequestDTO request,
            @RequestHeader("X-User-Email") String email
    ) {
        AppointmentResponseDTO response = appointmentService.createAppointment(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //GET /api/appointments
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointments(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        List<AppointmentResponseDTO> response = appointmentService.getAppointments(email, role);
        return ResponseEntity.ok(response);
    }

    //GET /api/appointments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        AppointmentResponseDTO response = appointmentService.getAppointmentById(id, email, role);
        return ResponseEntity.ok(response);
    }

    //PUT /api/appointments/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    //PUT /api/appointments/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    //PUT /api/appointments/{id}/mark-no-show
    @PutMapping("/{id}/mark-no-show")
    public ResponseEntity<AppointmentResponseDTO> markNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, "NO_SHOW"));
    }

    //DELETE /api/appointments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}