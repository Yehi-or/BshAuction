package com.bsh.bshauction.repository;

import java.math.BigDecimal;

public interface BidRepositoryCustom {
    Long deleteBidUserIdAndProductId(Long userId, Long productId, BigDecimal bidPrice);
}
