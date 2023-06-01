package com.bshauction.backend.repository;

import com.bshauction.backend.entity.BidHistory;
import com.bshauction.backend.entity.Product;
import com.bshauction.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Long> {
    Optional<BidHistory> findTop1ByProductAndAmountOrderByCreatedAt(Product product, BigDecimal amount);
    Long countByProductAndAmountAndUser(Product product, BigDecimal bidPrice, User user);

}
