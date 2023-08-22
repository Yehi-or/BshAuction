package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BidEndTimeDTO {
    private LocalDateTime bidEndTime;
    private LocalDateTime currentTime;
}
