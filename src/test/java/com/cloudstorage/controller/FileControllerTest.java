package com.cloudstorage.controller;

import com.cloudstorage.dto.FileResponse;
import com.cloudstorage.model.FileEntity;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.FileService;
import com.cloudstorage.security.JwtProvider;
import com.cloudstorage.repository.TokenRepository;
import com.cloudstorage.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@Import(SecurityConfig.class)
class FileControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private FileService fileService;
    @MockBean private UserRepository userRepository;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private TokenRepository tokenRepository;

    @Test
    @WithMockUser(username = "user")
    void getFilesSuccess() throws Exception {
        User user = User.builder().id(1L).login("user").password("pass").build();
        when(userRepository.findByLogin("user")).thenReturn(Optional.of(user));
        when(fileService.getUserFiles(user, 3))
                .thenReturn(List.of(FileEntity.builder().filename("f1.txt").size(100L).build()));

        mockMvc.perform(get("/cloud/list?limit=3").header("auth-token", "t"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename").value("f1.txt"));
    }

    @Test
    @WithMockUser(username = "user")
    void uploadFileSuccess() throws Exception {
        User user = User.builder().id(1L).login("user").password("pass").build();
        when(userRepository.findByLogin("user")).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());
        mockMvc.perform(multipart("/cloud/file").file(file).header("auth-token", "t"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteFileSuccess() throws Exception {
        User user = User.builder().id(1L).login("user").password("pass").build();
        when(userRepository.findByLogin("user")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/cloud/file?filename=test.txt").header("auth-token", "t"))
                .andExpect(status().isOk());
    }
}
