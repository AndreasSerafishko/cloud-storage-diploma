package com.cloudstorage.controller;

import com.cloudstorage.dto.FileResponse;
import com.cloudstorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/cloud")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getFiles(@RequestParam(defaultValue = "3") int limit) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(fileService.getUserFiles(login, limit));
    }
    
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            fileService.uploadFile(login, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"message\":\"Error uploading file\",\"id\":500}");
        }
    }
    
    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {
        try {
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            fileService.deleteFile(login, filename);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"message\":\"File not found\",\"id\":404}");
        }
    }
}
