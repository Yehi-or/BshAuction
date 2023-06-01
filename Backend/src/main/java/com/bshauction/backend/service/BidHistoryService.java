package com.bshauction.backend.service;

import com.bshauction.backend.entity.Bid;
import com.bshauction.backend.entity.BidHistory;
import com.bshauction.backend.entity.Product;
import com.bshauction.backend.entity.User;
import com.bshauction.backend.repository.BidHistoryRepository;
import com.bshauction.backend.repository.BidRepository;
import com.bshauction.backend.repository.ProductRepository;
import com.bshauction.backend.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidHistoryService {
    //@Transactional 롤백 처리에 대해서
    //기본적으로 @Transactional은 unchecked exception 에서 예외가 나면 롤백한다 checked 는 안함 -> 해줄려면 rollbackFor 옵션을 사용 (@Transactional은 내부 에서 try catch 문을 돌려서 사용함).
    private final BidHistoryRepository bidHistoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

    //입찰 시도 bidAttempt 메서드
    //중복 확인, 가격 비교, 입찰 기록 저장, 최고 입찰액 업데이트 등의 작업 모듈화 해야함.
    @Transactional
    public String bidAttempt(Long userId, BigDecimal bidPrice, Long productId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            //기존 상품보다 낮은 가격으로 입찰을 시도한지 확인 일단 프론트 단에서 막고 마지막 방어용
            if (product.getPrice().compareTo(bidPrice) > 0) {
                return "accessFail";
            }

            // 중복 확인
            long duplicateCount = bidHistoryRepository.countByProductAndAmountAndUser(product, bidPrice, user);

            if (duplicateCount > 0) {
                return "duplicate";
            }

            BidHistory bidHistory = BidHistory.builder()
                    .user(user)
                    .product(product)
                    .amount(bidPrice)
                    .build();

            bidHistoryRepository.save(bidHistory);

            // 가장 최근 입찰 기록 조회
            Optional<BidHistory> bidHistoryOptional = bidHistoryRepository.findTop1ByProductAndAmountOrderByCreatedAt(product, bidPrice);
            BidHistory findSameAttempt = bidHistoryOptional.orElse(null);

            if (findSameAttempt != null) {
                if (findSameAttempt.getUser().getUserId().equals(userId)) {
                    Bid bid = Bid.builder()
                            .user(user)
                            .product(product)
                            .amount(bidPrice)
                            .build();
                    bidRepository.save(bid);

                    BigDecimal highestBidAmount = bidRepository.findMaxPrice(product);

                    //상품 가격 업데이트 (가장 높은 가격으로)
                    if (highestBidAmount.compareTo(BigDecimal.ZERO) > 0) {
                        product.setPrice(highestBidAmount);
                    }
                    return "success";
                } else {
                    return "fail";
                }
            }
            return "error";
    }
}


