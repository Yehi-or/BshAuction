package com.bsh.bshauction.global.security.jwt.service;

import com.bsh.bshauction.entity.UserRefreshToken;
import com.bsh.bshauction.repository.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public UserRefreshToken saveRefreshToken(String refreshToken) {
        return UserRefreshToken.builder()
                .refreshToken(refreshToken)
                .build();
    }
}
