package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String storageFilename;
    
    @Column(nullable = false)
    private Long size;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private String filePath;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Builder.Default
    private LocalDateTime uploadDate = LocalDateTime.now();
}
