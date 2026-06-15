package com.cloudstorage.repository;

import com.cloudstorage.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndActiveTrue(String token);
}
