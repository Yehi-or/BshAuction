package com.bsh.bshauction.dto;

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
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long userId;

    @Builder
    public UserNickAndMessageDto(String userNick, String loginMessage, String refreshToken, String accessToken, String grantType, Long userId) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.userNick = userNick;
        this.loginMessage = loginMessage;
        this.grantType = grantType;
        this.userId = userId;
    }
}
