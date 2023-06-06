package com.bsh.bshauction.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "users")
@Slf4j
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(nullable = false)
    private String userNick;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userRefreshTokenId")
    private UserRefreshToken userRefreshToken;

    @ElementCollection
    private List<UserRole> role;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    public void addUserRole(UserRole userRole) {
        role.add(userRole);
    }

    @Builder
    public User(Long userId, String userEmail, String password, String userNick, List<UserRole> role, UserRefreshToken userRefreshToken) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.password = password;
        this.userNick = userNick;
        this.role = role;
        this.userRefreshToken = userRefreshToken;
    }
}
