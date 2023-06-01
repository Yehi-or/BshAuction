package com.bshauction.backend.controller;

import com.bshauction.backend.dto.BidDto;
import com.bshauction.backend.service.BidHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
public class bidController {

    private final BidHistoryService bidHistoryService;

    @PostMapping("/{itemId}")
    public void bidAttempt(@RequestBody BidDto bidDTO, @PathVariable Long itemId) {
        Long userId = bidDTO.getUserId();
        BigDecimal bidPrice = bidDTO.getBidPrice();
        String result = bidHistoryService.bidAttempt(userId, bidPrice, itemId);

    }
}
