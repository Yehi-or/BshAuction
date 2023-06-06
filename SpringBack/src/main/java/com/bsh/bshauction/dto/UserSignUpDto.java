package com.bsh.bshauction.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserSignUpDto {
    private String userEmail;
    private String password;
    private String userNick;
}