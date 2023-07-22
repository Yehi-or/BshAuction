package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.BidDto;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import com.bsh.bshauction.service.BidHistoryService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
public class BidController {
    private final BidHistoryService bidHistoryService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/accessTokenTest")
    public void test(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Object userId) {
        String accessToken = jwtTokenProvider.extractAccessToken(authorizationHeader);
        if(jwtTokenProvider.validateToken(accessToken)) {
            Claims claims = jwtTokenProvider.parseClaims(accessToken);
            Long userIds = claims.get("userId", Long.class);
            String userRole = claims.get("role", String.class);
        }
    }

}
