package com.bshauction.backend.service;

import com.bshauction.backend.dto.UserNickAndMessageDto;
import com.bshauction.backend.dto.UserSignInDto;
import com.bshauction.backend.dto.UserSignUpDto;
import com.bshauction.backend.entity.User;
import com.bshauction.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<UserNickAndMessageDto> userSignIn(UserSignInDto userSignInDto) {
        System.out.println(userSignInDto.getUserEmail());
        System.out.println(userSignInDto.getPassword());

        Optional<User> existingUser = userRepository.findUserByUserEmail(userSignInDto.getUserEmail());


        if (existingUser.isPresent()) {
            String userPassword = existingUser.get().getPassword();
            if (Objects.equals(userSignInDto.getPassword(), userPassword)) {
                UserNickAndMessageDto userNickAndMessageDto = UserNickAndMessageDto.builder()
                        .userNick(existingUser.get().getUserNick())
                        .loginMessage("success")
                        .build();
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
            User user = User.builder()
                    .userEmail(userSignUpDto.getUserEmail())
                    .password(userSignUpDto.getPassword())
                    .userNick(userSignUpDto.getUserNick())
                    .build();
            userRepository.save(user);
            return ResponseEntity.status(201).body("User Success Resister");
        }
    }
}
