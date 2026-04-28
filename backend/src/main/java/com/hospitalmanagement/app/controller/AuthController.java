package com.hospitalmanagement.app.controller;

import com.hospitalmanagement.app.dto.LoginRequestDTO;
import com.hospitalmanagement.app.dto.RegisterRequestDTO;
import com.hospitalmanagement.app.dto.UserResponseDTO;
import com.hospitalmanagement.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        // If the request reaches here, it means the JWT filter successfully authenticated the user.
        return ResponseEntity.ok("Token is valid");
    }
}
