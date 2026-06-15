package com.cloudstorage.controller;

import com.cloudstorage.dto.FileResponse;
import com.cloudstorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
            return ResponseEntity.status(500).body(Map.of("message", "Error uploading file", "id", 500));
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {
        try {
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            fileService.deleteFile(login, filename);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", "File not found", "id", 404));
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> updateFile(@RequestParam("filename") String filename,
                                        @RequestBody Map<String, String> body) {
        try {
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            String newFilename = body.get("filename");
            fileService.renameFile(login, filename, newFilename);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage(), "id", 404));
        }
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        try {
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            Path filePath = fileService.getFilePath(login, filename);
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
