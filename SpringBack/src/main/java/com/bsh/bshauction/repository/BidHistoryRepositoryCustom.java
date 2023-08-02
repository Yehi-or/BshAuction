package com.bsh.bshauction.repository;

import java.math.BigDecimal;

public interface BidHistoryRepositoryCustom {
    Long deleteBidHistoryUserIdAndProductId(Long userId, Long productId, BigDecimal bidPrice);

}
