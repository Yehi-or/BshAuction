package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnBidAttemptDTO {
    private String returnMessage;
    private String userNick;
    private Long userId;

    @Builder
    public ReturnBidAttemptDTO(String returnMessage, String userNick, Long userId) {
        this.returnMessage = returnMessage;
        this.userNick = userNick;
        this.userId = userId;
    }
}
