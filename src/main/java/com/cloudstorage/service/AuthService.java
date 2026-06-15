package com.cloudstorage.service;

import com.cloudstorage.dto.LoginResponse;
import com.cloudstorage.model.Token;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.TokenRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TokenRepository tokenRepository;
    
    @Autowired
    private JwtProvider jwtProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public LoginResponse login(String login, String password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtProvider.generateToken(user.getLogin());
        
        tokenRepository.save(Token.builder()
                .token(token)
                .user(user)
                .active(true)
                .build());
        
        return new LoginResponse(token);
    }
    
    public void logout(String authToken) {
        tokenRepository.findByTokenAndActiveTrue(authToken)
                .ifPresent(token -> {
                    token.setActive(false);
                    tokenRepository.save(token);
                });
    }
}
