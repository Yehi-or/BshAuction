package com.bsh.bshauction.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "user_refresh_token")
@NoArgsConstructor
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRefreshTokenId;

    @Column
    private String refreshToken;

    @Builder
    public UserRefreshToken(Long userRefreshTokenId, String refreshToken) {
        this.userRefreshTokenId = userRefreshTokenId;
        this.refreshToken = refreshToken;
    }
}
