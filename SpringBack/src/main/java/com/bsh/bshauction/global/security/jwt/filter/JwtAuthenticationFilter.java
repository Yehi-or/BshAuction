package com.bsh.bshauction.global.security.jwt.filter;

import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    //명시적으로 authenticate 를 불러서 UserDetailsServiceImpl 에서 User(security.core.userdetails) 를 리턴해서 authentication 을 리턴해도 된다
    //현재 방법은 새로운 userDetails 를 만들어서 거기에 유저 토큰에 포함된 유저 정보를 가지고 권한을 부여한다.
    //필터 2번 실행되서 변경 doFilter 빈으로 등록되어 있으면 1번 실행되고 apply 에서 한번 더 실행되면서 총 2번이 실행됌.
    //스프링 부트는 빈으로 등록된것 중에 필터가 있다면 자동으로 등록하고 실행함 여기서 1번 aplly 에서 1번더 총 2번 실행.
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 1. Request Header 에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사.
        if(token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            logger.info("-success accessToken-");

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if(token != null && !jwtTokenProvider.validateToken(token)) {
            //accessToken 만료
            //response 리턴 401 에러를 전달한다
            logger.info("-expired accessToken-");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("token expired");

            return;
        }

        filterChain.doFilter(request, response);
    }


    //Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
