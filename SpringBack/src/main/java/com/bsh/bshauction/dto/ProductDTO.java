package com.bsh.bshauction.dto;

import com.bsh.bshauction.entity.Bid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ProductDTO {
    private String productName;
    private BigDecimal price;
    private List<BigDecimal> bidList;
}
