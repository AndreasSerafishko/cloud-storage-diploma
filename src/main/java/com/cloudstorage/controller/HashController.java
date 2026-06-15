package com.cloudstorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class HashController {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/hash")
    public String hash(@RequestParam String p) {
        return passwordEncoder.encode(p);
    }
}
