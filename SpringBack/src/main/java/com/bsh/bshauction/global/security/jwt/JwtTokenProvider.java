package com.bsh.bshauction.global.security.jwt;

import com.bsh.bshauction.dto.jwt.TokenInfo;
import com.bsh.bshauction.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final UserRepository userRepository;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.userRepository = userRepository;
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //토큰 생성
    public TokenInfo generateToken(Long userId, String role) {

//        권한 가져오기 (userLogin 에서 명시적으로 UserNameAndPasswordToken 만들때 매개변수는 Authentication authentication)
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));

        Map<String , Object> claims = setClaims(userId, role);
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 86400 * 1000);

        //Access Token 생성
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        //Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Map<String, Object> setClaims(Long userId, String role) {
        return Map.of("userId", userId, "role", role);
    }

    @Transactional(readOnly = true)
    public String refreshAccessToken(String refreshToken) {
        if(validateToken(refreshToken)) {
            Optional<com.bsh.bshauction.entity.User> user = userRepository.findByUserRefreshToken_RefreshToken(refreshToken);
            if(user.isPresent()) {
                Long userId = user.get().getUserId();
                String role = user.get().getRole().toString();

                Map<String , Object> claims = setClaims(userId, role);
                long now = (new Date()).getTime();

                //Access Token 생성
                Date accessTokenExpiresIn = new Date(now + 86400 * 1000);

                return Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(accessTokenExpiresIn)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
            }
            return "이상한 토큰 값";
        }
        return "로그아웃";
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if(claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Object roleObject = claims.get("role");

        if (!(roleObject instanceof String)) {
            throw new RuntimeException("올바르지 않은 권한 정보입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //UserDetails 객체를 만들어서 Authentication 리턴
        User principal = new User(claims.get("userId").toString(), "", authorities);

        log.info("---principal : " + principal);

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch(ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch(UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch(IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String extractAccessToken(String authorizationHeader) {
        String tokenPrefix = "Bearer ";
        if (authorizationHeader.startsWith(tokenPrefix)) {
            return authorizationHeader.substring(tokenPrefix.length());
        } else {
            throw new IllegalArgumentException("Invalid access token");
        }
    }
}

