package com.cloudstorage.service;

import com.cloudstorage.model.FileEntity;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock private FileRepository fileRepository;
    @InjectMocks private FileService fileService;

    @Test
    void getUserFiles() {
        User u = User.builder().id(1L).login("user").build();
        when(fileRepository.findByUserIdOrderByUploadDateDesc(1L))
                .thenReturn(List.of(FileEntity.builder().filename("f1.txt").size(10L).build()));
        assertEquals(1, fileService.getUserFiles(u, 3).size());
    }

    @Test
    void deleteFileNotFound() {
        User u = User.builder().id(1L).build();
        when(fileRepository.findByFilenameAndUserId("x.txt", 1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> fileService.deleteFile(u, "x.txt"));
    }
}
