package com.cloudstorage.service;

import com.cloudstorage.model.FileEntity;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Value("${storage.path}")
    private String storagePath;

    @Autowired
    private FileRepository fileRepository;

    public List<FileEntity> getUserFiles(User user, int limit) {
        return fileRepository.findByUserIdOrderByUploadDateDesc(user.getId())
                .stream().limit(limit).toList();
    }

    @Transactional
    public void uploadFile(User user, MultipartFile file) throws IOException {
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
    public void deleteFile(User user, String filename) {
        FileEntity file = fileRepository.findByFilenameAndUserId(filename, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found"));
        try { Files.deleteIfExists(Paths.get(file.getFilePath())); } catch (IOException e) {}
        fileRepository.deleteByFilenameAndUserId(filename, user.getId());
    }

    @Transactional
    public void renameFile(User user, String oldFilename, String newFilename) {
        FileEntity file = fileRepository.findByFilenameAndUserId(oldFilename, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found"));
        file.setFilename(newFilename);
        fileRepository.save(file);
    }

    public Path getFilePath(User user, String filename) {
        FileEntity file = fileRepository.findByFilenameAndUserId(filename, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found"));
        return Paths.get(file.getFilePath());
    }
}
