package com.bshauction.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignInDto {
    private String userEmail;
    private String password;
}
