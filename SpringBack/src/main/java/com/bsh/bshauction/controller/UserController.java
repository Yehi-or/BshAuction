package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.UserNickAndMessageDto;
import com.bsh.bshauction.dto.UserSignInDto;
import com.bsh.bshauction.dto.UserSignUpDto;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import com.bsh.bshauction.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //테스트 용도
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signUp")
    public ResponseEntity<String> userSignUp(@RequestBody UserSignUpDto userSignUpDto) {
        return userService.userSignUp(userSignUpDto);
    }

    @PostMapping("/signIn")
    public ResponseEntity<UserNickAndMessageDto> userSignIn(@RequestBody UserSignInDto userSignInDto) {
        return userService.userSignIn(userSignInDto);
    }
}
