package com.bsh.bshauction.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.bsh.bshauction.entity.QBid.bid;

@Repository
@RequiredArgsConstructor
public class BidRepositoryImpl implements BidRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

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
