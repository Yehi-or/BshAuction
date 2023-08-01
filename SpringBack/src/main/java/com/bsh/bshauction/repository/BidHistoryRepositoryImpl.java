package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.BidHistory;
import com.bsh.bshauction.entity.Product;
import com.bsh.bshauction.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;

import static com.bsh.bshauction.entity.QBid.bid;
import static com.bsh.bshauction.entity.QBidHistory.bidHistory;

@Repository
@RequiredArgsConstructor
public class BidHistoryRepositoryImpl implements BidHistoryRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long deleteBidHistoryUserIdAndProductId(Long userId, Long productId, BigDecimal bidPrice) {

        return jpaQueryFactory
                .delete(bidHistory)
                .where(bidHistory.user.userId.eq(userId)
                        .and(bidHistory.product.productId.eq(productId))
                        .and(bidHistory.amount.eq(bidPrice)))
                .execute();
    }

    @Override
    public Long deleteBidUserIdAndProductId(Long userId, Long productId, BigDecimal bidPrice) {

        return jpaQueryFactory
                .delete(bid)
                .where(bid.user.userId.eq(userId)
                        .and(bid.product.productId.eq(productId))
                        .and(bid.amount.eq(bidPrice)))
                .execute();

    }
}
