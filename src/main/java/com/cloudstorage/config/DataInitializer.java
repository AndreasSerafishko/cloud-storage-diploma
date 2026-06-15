package com.cloudstorage.config;

import com.cloudstorage.model.User;
import com.cloudstorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (userRepository.findByLogin("user").isEmpty()) {
            userRepository.save(User.builder()
                    .login("user")
                    .password(passwordEncoder.encode("password"))
                    .build());
            System.out.println("Created user: user / password");
        }
        
        if (userRepository.findByLogin("test").isEmpty()) {
            userRepository.save(User.builder()
                    .login("test")
                    .password(passwordEncoder.encode("test123"))
                    .build());
            System.out.println("Created user: test / test123");
        }
    }
}
