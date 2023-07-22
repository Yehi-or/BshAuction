package com.bsh.bshauction.service;

import com.bsh.bshauction.dto.BidMainReturnDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class StompService {

    public BidMainReturnDTO returnMain(Long productId, BigDecimal price) {
        //메인페이지 가격변경
        return BidMainReturnDTO.builder()
                .productId(productId)
                .productPrice(price)
                .build();
    }


}
