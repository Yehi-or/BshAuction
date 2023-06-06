package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.User;
import com.bsh.bshauction.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUserEmail(String userEmail);
    Optional<User> findByUserRefreshToken_RefreshToken(String refreshToken);
    Optional<User> findUserByUserId(Long userId);
}
