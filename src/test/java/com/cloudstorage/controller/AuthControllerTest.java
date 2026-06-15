package com.cloudstorage.controller;

import com.cloudstorage.dto.LoginRequest;
import com.cloudstorage.dto.LoginResponse;
import com.cloudstorage.service.AuthService;
import com.cloudstorage.security.JwtProvider;
import com.cloudstorage.repository.TokenRepository;
import com.cloudstorage.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private TokenRepository tokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("user");
        request.setPassword("password");

        when(authService.login("user", "password"))
                .thenReturn(new LoginResponse("test-token-123"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token").value("test-token-123"));
    }

    @Test
    void loginFailure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("user");
        request.setPassword("wrong");

        when(authService.login("user", "wrong"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutSuccess() throws Exception {
        mockMvc.perform(post("/logout")
                        .header("auth-token", "test-token"))
                .andExpect(status().isOk());
    }
}
