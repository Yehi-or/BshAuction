package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class ReturnUpdateProductBidDTO {
    private boolean returnBool;
    private BigDecimal returnMostPrice;
}
