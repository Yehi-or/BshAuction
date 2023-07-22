package com.bsh.bshauction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class ProductListDTO {
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
}
