package com.cloudstorage.service;

import com.cloudstorage.dto.FileResponse;
import com.cloudstorage.model.FileEntity;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FileRepository;
import com.cloudstorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {
    
    @Value("${storage.path}")
    private String storagePath;
    
    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<FileResponse> getUserFiles(String login, int limit) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return fileRepository.findByUserIdOrderByUploadDateDesc(user.getId())
                .stream()
                .limit(limit)
                .map(f -> new FileResponse(f.getFilename(), f.getSize()))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void uploadFile(String login, MultipartFile file) throws IOException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String storageFilename = UUID.randomUUID().toString();
        Path userDir = Paths.get(storagePath, user.getLogin());
        Files.createDirectories(userDir);
        
        Path filePath = userDir.resolve(storageFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        fileRepository.findByFilenameAndUserId(file.getOriginalFilename(), user.getId())
                .ifPresent(old -> {
                    try { Files.deleteIfExists(Paths.get(old.getFilePath())); } catch (IOException e) {}
                    fileRepository.delete(old);
                });
        
        fileRepository.save(FileEntity.builder()
                .filename(file.getOriginalFilename())
                .storageFilename(storageFilename)
                .size(file.getSize())
                .contentType(file.getContentType())
                .filePath(filePath.toString())
                .user(user)
                .build());
    }
    
    @Transactional
    public void deleteFile(String login, String filename) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        FileEntity file = fileRepository.findByFilenameAndUserId(filename, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found"));
        
        try { Files.deleteIfExists(Paths.get(file.getFilePath())); } catch (IOException e) {}
        fileRepository.deleteByFilenameAndUserId(filename, user.getId());
    }
}
