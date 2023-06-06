package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.Bid;
import com.bsh.bshauction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT MAX(amount) FROM Bid WHERE product = :product")
    BigDecimal findMaxPrice(@Param("product") Product product);
}