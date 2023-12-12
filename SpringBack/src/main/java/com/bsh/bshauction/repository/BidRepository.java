package com.bsh.bshauction.repository;

import com.bsh.bshauction.dto.BidListDTO;
import com.bsh.bshauction.entity.Bid;
import com.bsh.bshauction.entity.Product;
import com.bsh.bshauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long>, BidRepositoryCustom {

//    @Lock(LockModeType.OPTIMISTIC) 조회할때 바로 버전 확인
    @Query("SELECT MAX(amount) FROM Bid WHERE product = :product")
    BigDecimal findMaxPrice(@Param("product") Product product);

    @Query("SELECT NEW com.bsh.bshauction.dto.BidListDTO(bid.amount, user.userNick, bid.user.userId) FROM Bid bid JOIN  User user on user.userId = bid.user.userId WHERE bid.product = :product ORDER BY bid.amount ASC")
    List<BidListDTO> findByProductOrderByAmountAsc(@Param("product") Product product);

}