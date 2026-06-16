package com.cloudstorage.controller;

import com.cloudstorage.dto.FileResponse;
import com.cloudstorage.model.FileEntity;
import com.cloudstorage.model.User;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cloud")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getFiles(@RequestParam(defaultValue = "3") int limit) {
        User user = getCurrentUser();
        log.info("Getting file list for user: {}", user.getLogin());
        List<FileEntity> files = fileService.getUserFiles(user, limit);
        List<FileResponse> response = files.stream()
                .map(f -> new FileResponse(f.getFilename(), f.getSize()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        User user = getCurrentUser();
        log.info("Uploading file: {} for user: {}", file.getOriginalFilename(), user.getLogin());
        try {
            fileService.uploadFile(user, file);
            log.info("File uploaded: {}", file.getOriginalFilename());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error uploading file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Error uploading file", "id", 500));
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {
        User user = getCurrentUser();
        log.info("Deleting file: {} for user: {}", filename, user.getLogin());
        try {
            fileService.deleteFile(user, filename);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting file: {}", filename, e);
            return ResponseEntity.status(404).body(Map.of("message", "File not found", "id", 404));
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> updateFile(@RequestParam("filename") String filename,
                                        @RequestBody Map<String, String> body) {
        User user = getCurrentUser();
        String newFilename = body.get("filename");
        log.info("Renaming file: {} -> {} for user: {}", filename, newFilename, user.getLogin());
        try {
            fileService.renameFile(user, filename, newFilename);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error renaming file: {}", filename, e);
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage(), "id", 404));
        }
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        User user = getCurrentUser();
        log.info("Downloading file: {} for user: {}", filename, user.getLogin());
        try {
            Path filePath = fileService.getFilePath(user, filename);
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading file: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    private User getCurrentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
