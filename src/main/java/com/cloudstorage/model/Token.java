package com.cloudstorage.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 500)
    private String token;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Builder.Default
    private boolean active = true;
}
