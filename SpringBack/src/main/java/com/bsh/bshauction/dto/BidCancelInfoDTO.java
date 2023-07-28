package com.bsh.bshauction.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BidCancelInfoDTO {
    private BigDecimal bidPrice;
    private String bidUserName;
    private Long userId;
}
