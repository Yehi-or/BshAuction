package com.bsh.bshauction.global.security.jwt.controller;

import com.bsh.bshauction.dto.jwt.RefreshTokenDto;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refreshAccessToken")
    public ResponseEntity<String> refreshAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();
        return jwtTokenProvider.refreshAccessToken(refreshToken);
    }
}
