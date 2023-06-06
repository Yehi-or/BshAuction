package com.bsh.bshauction.global.security.jwt.controller;

import com.bsh.bshauction.dto.jwt.RefreshTokenDto;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
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
    public void refreshAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();
        System.out.println(refreshToken);
        String access = jwtTokenProvider.refreshAccessToken(refreshToken);
        System.out.println(access);
    }
}
