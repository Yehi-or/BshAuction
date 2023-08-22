package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReturnBidAttemptDTO {
    private String returnMessage;
    private String userNick;
    private Long userId;
    private LocalDateTime endTime;

    @Builder
    public ReturnBidAttemptDTO(String returnMessage, String userNick, Long userId, LocalDateTime endTime) {
        this.returnMessage = returnMessage;
        this.userNick = userNick;
        this.userId = userId;
        this.endTime = endTime;
    }
}
