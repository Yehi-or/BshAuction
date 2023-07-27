package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BidListDTO {
    private BigDecimal bidPrice;
    private String bidUserName;
    private Long userId;

    @Builder
    public BidListDTO(BigDecimal bidPrice, String bidUserName, Long userId) {
        this.bidPrice = bidPrice;
        this.bidUserName = bidUserName;
        this.userId = userId;
    }
}
