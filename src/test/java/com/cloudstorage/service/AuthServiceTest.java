package com.cloudstorage.service;

import com.cloudstorage.dto.LoginResponse;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.TokenRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private JwtProvider jwtProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private AuthService authService;

    @Test
    void loginSuccess() {
        User user = User.builder().id(1L).login("user").password("hash").build();
        when(userRepository.findByLogin("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hash")).thenReturn(true);
        when(jwtProvider.generateToken("user")).thenReturn("token123");

        LoginResponse r = authService.login("user", "password");
        assertEquals("token123", r.getAuthToken());
        verify(tokenRepository).save(any());
    }

    @Test
    void loginFailure() {
        when(userRepository.findByLogin("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.login("user", "pass"));
    }
}
