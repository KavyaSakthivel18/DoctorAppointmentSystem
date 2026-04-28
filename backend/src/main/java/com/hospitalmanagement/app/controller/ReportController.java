package com.hospitalmanagement.app.controller;

import com.hospitalmanagement.app.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping("/daily-summary")
    public ResponseEntity<Map<String, Object>> getDailySummary(@RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        long totalAppointments = appointmentRepository.countAppointments(parsedDate, null, null);
        Double totalRevenue = appointmentRepository.calculateRevenue(parsedDate, null, null);
        
        return ResponseEntity.ok(Map.of(
                "date", parsedDate,
                "totalAppointments", totalAppointments,
                "revenue", totalRevenue != null ? totalRevenue : 0.0
        ));
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) Long specialty) {
        
        LocalDate parsedDate = date != null ? LocalDate.parse(date) : null;
        Double revenue = appointmentRepository.calculateRevenue(parsedDate, mode, specialty);
        
        return ResponseEntity.ok(Map.of(
                "revenue", revenue != null ? revenue : 0.0,
                "mode", mode != null ? mode : "ALL",
                "specialtyId", specialty != null ? String.valueOf(specialty) : "ALL"
        ));
    }

    @GetMapping("/appointments/count")
    public ResponseEntity<Map<String, Object>> getAppointmentsCount(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) Long specialty) {
        
        LocalDate parsedDate = date != null ? LocalDate.parse(date) : null;
        long count = appointmentRepository.countAppointments(parsedDate, mode, specialty);
        
        return ResponseEntity.ok(Map.of(
                "count", count,
                "mode", mode != null ? mode : "ALL",
                "specialtyId", specialty != null ? String.valueOf(specialty) : "ALL"
        ));
    }
}
