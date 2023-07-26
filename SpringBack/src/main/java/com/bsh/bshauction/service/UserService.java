package com.bsh.bshauction.service;

import com.bsh.bshauction.dto.UserNickAndMessageDto;
import com.bsh.bshauction.dto.UserSignInDto;
import com.bsh.bshauction.dto.UserSignUpDto;
import com.bsh.bshauction.dto.jwt.TokenInfo;
import com.bsh.bshauction.entity.User;
import com.bsh.bshauction.entity.UserRefreshToken;
import com.bsh.bshauction.entity.UserRole;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import com.bsh.bshauction.repository.UserRefreshTokenRepository;
import com.bsh.bshauction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public ResponseEntity<UserNickAndMessageDto> userSignIn(UserSignInDto userSignInDto) {

        log.info("----- userEmail : " + userSignInDto.getUserEmail());

        Optional<User> existingUser = userRepository.findUserByUserEmail(userSignInDto.getUserEmail());

        if (existingUser.isPresent()) {
            String userPassword = existingUser.get().getPassword();
            if (passwordEncoder.matches(userSignInDto.getPassword(), userPassword)) {

                Long userId = existingUser.get().getUserId();

                String role = existingUser.get().getRole().stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(","));

                log.info("userId : " + userId + " role : " + role);

//                명시적으로 UsernamePasswordAuthenticationToken 토큰을 만들고 authenticate 로 UserDetailsService 를 불러와서 authenticationToken 을 가져오는 방법 userDetails 를 반환받아서
//                SecurityContextHolder 에 authentication 를 추가한다.
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userSignInDto.getUserEmail(), userSignInDto.getPassword());
//                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//                SecurityContextHolder.getContext().setAuthentication(authentication);

                TokenInfo tokenInfo = jwtTokenProvider.generateToken(userId, role);

                UserNickAndMessageDto userNickAndMessageDto = UserNickAndMessageDto.builder()
                        .userNick(existingUser.get().getUserNick())
                        .loginMessage("success")
                        .grantType(tokenInfo.getGrantType())
                        .accessToken(tokenInfo.getAccessToken())
                        .refreshToken(tokenInfo.getRefreshToken())
                        .userId(userId)
                        .build();

                UserRefreshToken userRefreshToken = existingUser.get().getUserRefreshToken();
                userRefreshToken.setRefreshToken(tokenInfo.getRefreshToken());

                log.info("access : " + tokenInfo.getAccessToken() + " refresh : " + tokenInfo.getRefreshToken());
                return ResponseEntity.status(201).body(userNickAndMessageDto);
            }
            UserNickAndMessageDto userNickAndMessageDto = UserNickAndMessageDto.builder()
                    .loginMessage("passwordFail")
                    .build();
            return ResponseEntity.status(201).body(userNickAndMessageDto);
        }
        UserNickAndMessageDto userNickAndMessageDto = UserNickAndMessageDto.builder()
                .loginMessage("idFail")
                .build();
        return ResponseEntity.status(201).body(userNickAndMessageDto);
    }

    @Transactional
    public ResponseEntity<String> userSignUp(UserSignUpDto userSignUpDto) {
        System.out.println(userSignUpDto.getUserEmail());
        System.out.println(userSignUpDto.getUserNick());

        Optional<User> existingUser = userRepository.findUserByUserEmail(userSignUpDto.getUserEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(419).body("User already exist");
        } else {
            UserRefreshToken userRefreshToken = UserRefreshToken.builder()
                    .refreshToken(null)
                    .build();
            userRefreshTokenRepository.save(userRefreshToken);

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(userSignUpDto.getPassword());

            User user = User.builder()
                    .userEmail(userSignUpDto.getUserEmail())
                    .password(encodedPassword)
                    .userNick(userSignUpDto.getUserNick())
                    .role(new ArrayList<>())
                    .userRefreshToken(userRefreshToken)
                    .build();
            user.addUserRole(UserRole.ROLE_USER);
            userRepository.save(user);
            return ResponseEntity.status(201).body("User Success Resister");
        }
    }
}

