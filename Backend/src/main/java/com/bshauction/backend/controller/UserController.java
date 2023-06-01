package com.bshauction.backend.controller;

import com.bshauction.backend.dto.UserNickAndMessageDto;
import com.bshauction.backend.dto.UserSignInDto;
import com.bshauction.backend.dto.UserSignUpDto;
import com.bshauction.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<String> userSignUp(@RequestBody UserSignUpDto userSignUpDto) {
        return userService.userSignUp(userSignUpDto);
    }

    @PostMapping("/signIn")
    public ResponseEntity<UserNickAndMessageDto> userSignIn(@RequestBody UserSignInDto userSignInDto) {
        return userService.userSignIn(userSignInDto);
    }
}
