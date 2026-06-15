package com.cloudstorage.repository;

import com.cloudstorage.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByUserIdOrderByUploadDateDesc(Long userId);
    Optional<FileEntity> findByFilenameAndUserId(String filename, Long userId);
    void deleteByFilenameAndUserId(String filename, Long userId);
}
