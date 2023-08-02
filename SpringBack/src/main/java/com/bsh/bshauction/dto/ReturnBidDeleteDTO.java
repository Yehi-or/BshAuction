package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ReturnBidDeleteDTO {
    private String returnTypeString;
    private BigDecimal updateBidPrice;
}
