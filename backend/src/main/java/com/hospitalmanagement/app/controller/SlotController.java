package com.hospitalmanagement.app.controller;

import com.hospitalmanagement.app.dto.AvailableSlotRequestDTO;
import com.hospitalmanagement.app.entity.AvailableSlot;
import com.hospitalmanagement.app.repository.AvailableSlotRepository;
import com.hospitalmanagement.app.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SlotController {

    private final AvailableSlotRepository slotRepository;
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<List<AvailableSlot>> createSlots(@RequestBody AvailableSlotRequestDTO dto) {
        // Assuming dto contains doctorId
        return ResponseEntity.ok(doctorService.addAvailableSlot(dto.getDoctorId(), dto));
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableSlot>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        return ResponseEntity.ok(slotRepository.findByDoctorIdAndDateAndIsAvailable(doctorId, parsedDate, true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailableSlot> updateSlot(@PathVariable Long id, @RequestBody AvailableSlot slotDetails) {
        AvailableSlot slot = slotRepository.findById(id).orElseThrow(() -> new RuntimeException("Slot not found"));
        slot.setStartTime(slotDetails.getStartTime());
        slot.setEndTime(slotDetails.getEndTime());
        slot.setDate(slotDetails.getDate());
        return ResponseEntity.ok(slotRepository.save(slot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        slotRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity<AvailableSlot> lockSlot(@PathVariable Long id) {
        AvailableSlot slot = slotRepository.findById(id).orElseThrow(() -> new RuntimeException("Slot not found"));
        if (!slot.isAvailable()) {
            throw new RuntimeException("Slot is already locked");
        }
        slot.setAvailable(false);
        return ResponseEntity.ok(slotRepository.save(slot));
    }
}
