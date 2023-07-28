package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.BidHistory;
import com.bsh.bshauction.entity.Product;
import com.bsh.bshauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Long> {
    Optional<BidHistory> findTop1ByProductAndAmountOrderByCreatedAt(Product product, BigDecimal amount);
    Long countByProductAndAmountAndUser(Product product, BigDecimal bidPrice, User user);
    Long deleteByUserIdAndProductIdAndBidPrice(Long userId, Long productId, BigDecimal bidPrice);


}
