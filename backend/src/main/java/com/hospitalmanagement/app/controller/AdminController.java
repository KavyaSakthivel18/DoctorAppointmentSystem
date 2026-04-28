
package com.hospitalmanagement.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.hospitalmanagement.app.dto.AdminDTO;
import com.hospitalmanagement.app.entity.Admin;
import com.hospitalmanagement.app.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public Admin registerAdmin(@RequestBody AdminDTO adminDTO) {
        return adminService.registerAdmin(adminDTO);
    }

    @GetMapping("/all")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{email}")
    public Admin getAdminByEmail(@PathVariable String email) {
        return adminService.getAdminByEmail(email);
    }

    @Autowired
    private com.hospitalmanagement.app.repository.UserRepository userRepository;

    @Autowired
    private com.hospitalmanagement.app.repository.AppointmentRepository appointmentRepository;

    @GetMapping("/appointments")
    public ResponseEntity<List<com.hospitalmanagement.app.entity.Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentRepository.findAll());
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<com.hospitalmanagement.app.entity.User>> getAllDoctors() {
        return ResponseEntity.ok(userRepository.findByRole(com.hospitalmanagement.app.entity.Role.DOCTOR));
    }

    @GetMapping("/users")
    public ResponseEntity<List<com.hospitalmanagement.app.entity.User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        // Find the doctor
        com.hospitalmanagement.app.entity.User doctor = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Delete their appointments first to avoid foreign key constraints
        List<com.hospitalmanagement.app.entity.Appointment> appointments = appointmentRepository.findByDoctor(doctor);
        appointmentRepository.deleteAll(appointments);
        
        // Delete the doctor (available slots will be cascaded)
        userRepository.delete(doctor);
        return ResponseEntity.ok().body("{\"message\": \"Doctor deleted successfully\"}");
    }
}
