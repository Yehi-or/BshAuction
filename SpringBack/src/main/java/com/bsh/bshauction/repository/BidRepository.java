package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.Bid;
import com.bsh.bshauction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

//    @Lock(LockModeType.OPTIMISTIC) 조회할때 바로 버전 확인
    @Query("SELECT MAX(amount) FROM Bid WHERE product = :product")
    BigDecimal findMaxPrice(@Param("product") Product product);

    @Query("SELECT bid.amount FROM Bid bid WHERE bid.product = :product ORDER BY bid.amount ASC")
    List<BigDecimal> findByProductOrderByAmountAsc(@Param("product") Product product);
}