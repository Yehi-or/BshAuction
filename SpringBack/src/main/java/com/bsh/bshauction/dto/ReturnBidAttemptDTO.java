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

    @Builder
    public ReturnBidAttemptDTO(String returnMessage, String userNick) {
        this.returnMessage = returnMessage;
        this.userNick = userNick;
    }
}
