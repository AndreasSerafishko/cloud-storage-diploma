package com.cloudstorage.controller;

import com.cloudstorage.dto.FileResponse;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@Import(SecurityConfig.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private TokenRepository tokenRepository;

    @Test
    @WithMockUser(username = "user")
    void getFilesSuccess() throws Exception {
        List<FileResponse> files = Arrays.asList(
                new FileResponse("file1.txt", 100L),
                new FileResponse("file2.txt", 200L)
        );

        when(fileService.getUserFiles("user", 3)).thenReturn(files);

        mockMvc.perform(get("/cloud/list?limit=3")
                        .header("auth-token", "test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].filename").value("file1.txt"))
                .andExpect(jsonPath("$[0].size").value(100));
    }

    @Test
    @WithMockUser(username = "user")
    void uploadFileSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello".getBytes()
        );

        mockMvc.perform(multipart("/cloud/file")
                        .file(file)
                        .header("auth-token", "test-token"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteFileSuccess() throws Exception {
        mockMvc.perform(delete("/cloud/file?filename=test.txt")
                        .header("auth-token", "test-token"))
                .andExpect(status().isOk());
    }
}
