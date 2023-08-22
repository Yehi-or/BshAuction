package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class BidReturnDTO {
    private ReturnBidAttemptDTO returnBidAttemptDTO;
    private BigDecimal tryPrice;
    private Long userId;
}
