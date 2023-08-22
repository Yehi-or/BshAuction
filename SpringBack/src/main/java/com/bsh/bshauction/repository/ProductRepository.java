package com.bsh.bshauction.repository;

import com.bsh.bshauction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.finishAt = :newFinishAt WHERE p.productId = :productId")
    boolean updateFinishAt(@Param("productId") Long productId, @Param("newFinishAt") LocalDateTime newFinishAt);
}
