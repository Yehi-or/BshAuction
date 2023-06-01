package com.bshauction.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserNickAndMessageDto {
    private String userNick;
    private String loginMessage;

    @Builder
    public UserNickAndMessageDto(String userNick, String loginMessage) {
        this.userNick = userNick;
        this.loginMessage = loginMessage;
    }
}
