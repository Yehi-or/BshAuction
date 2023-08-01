package com.bsh.bshauction.service;

import com.bsh.bshauction.dto.BidCancelInfoDTO;
import com.bsh.bshauction.repository.BidHistoryRepository;
import com.bsh.bshauction.repository.BidHistoryRepositoryImpl;
import com.bsh.bshauction.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class BidService {
    private final BidHistoryRepositoryImpl bidHistoryRepository;

    @Transactional
    public boolean deleteBidHistory(BidCancelInfoDTO bidCancelInfoDTO, Long productId) {
        Long userId = bidCancelInfoDTO.getUserId();
        BigDecimal bidProductPrice = bidCancelInfoDTO.getBidPrice();

        Long deleteRows = bidHistoryRepository.deleteBidHistoryUserIdAndProductId(userId, productId, bidProductPrice);

        if(deleteRows > 0) {
            Long deleteBidRows = bidHistoryRepository.deleteBidUserIdAndProductId(userId, productId, bidProductPrice);
            return deleteBidRows > 0;
        }

        return false;
    }
}
