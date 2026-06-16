package com.cloudstorage.controller;

import com.cloudstorage.dto.LoginRequest;
import com.cloudstorage.dto.LoginResponse;
import com.cloudstorage.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getLogin());
        try {
            LoginResponse response = authService.login(request.getLogin(), request.getPassword());
            log.info("Login successful for user: {}", request.getLogin());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Login failed for user: {}", request.getLogin());
            return ResponseEntity.status(401).body("{\"message\":\"Invalid credentials\",\"id\":401}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String token) {
        log.info("Logout request");
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}
