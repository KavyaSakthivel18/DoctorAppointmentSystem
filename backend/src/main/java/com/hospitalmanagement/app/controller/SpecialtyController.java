package com.hospitalmanagement.app.controller;

import com.hospitalmanagement.app.entity.Department;
import com.hospitalmanagement.app.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpecialtyController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<List<Department>> getAllSpecialties() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Department> addSpecialty(@RequestBody Department department) {
        return ResponseEntity.ok(departmentRepository.save(department));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateSpecialty(@PathVariable Long id, @RequestBody Department department) {
        Department existing = departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Specialty not found"));
        existing.setName(department.getName());
        existing.setConsultationFee(department.getConsultationFee());
        return ResponseEntity.ok(departmentRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Long id) {
        departmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
