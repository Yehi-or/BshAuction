package com.bsh.bshauction.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.bsh.bshauction.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean updateProductFinishTime(Long productId, LocalDateTime newUpdateFinishTime) {
        return true;
    }

}
