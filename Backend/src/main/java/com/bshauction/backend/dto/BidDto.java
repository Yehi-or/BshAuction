package com.bshauction.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class BidDto {
    private Long userId;
    private BigDecimal bidPrice;
}
