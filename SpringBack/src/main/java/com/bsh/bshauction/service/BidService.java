package com.bsh.bshauction.service;

import com.bsh.bshauction.dto.BidCancelInfoDTO;
import com.bsh.bshauction.repository.BidHistoryRepository;
import com.bsh.bshauction.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final BidHistoryRepository bidHistoryRepository;

    public boolean deleteBidHistory(BidCancelInfoDTO bidCancelInfoDTO) {
        Long userId = bidCancelInfoDTO.getUserId();
        BigDecimal bidProductPrice = bidCancelInfoDTO.getBidPrice();


    }
}
