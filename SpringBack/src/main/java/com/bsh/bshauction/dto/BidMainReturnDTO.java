package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BidMainReturnDTO {
    private Long productId;
    private BigDecimal productPrice;
}
