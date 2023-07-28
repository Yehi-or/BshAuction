package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.BidHistory;
import com.bsh.bshauction.entity.Product;
import com.bsh.bshauction.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BidHistoryRepositoryImpl implements BidHistoryRepository {
    
    private EntityManager entityManager;

    @Override
    public Optional<BidHistory> findTop1ByProductAndAmountOrderByCreatedAt(Product product, BigDecimal amount) {
        return Optional.empty();
    }

    @Override
    public Long countByProductAndAmountAndUser(Product product, BigDecimal bidPrice, User user) {
        return null;
    }

    @Override
    @Transactional
    public Long deleteByUserIdAndProductIdAndBidPrice(Long userId, Long productId, BigDecimal bidPrice) {
        
    }
}
