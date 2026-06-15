package com.cloudstorage.controller;

import com.cloudstorage.dto.LoginRequest;
import com.cloudstorage.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request.getLogin(), request.getPassword()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("{\"message\":\"Invalid credentials\",\"id\":401}");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}
