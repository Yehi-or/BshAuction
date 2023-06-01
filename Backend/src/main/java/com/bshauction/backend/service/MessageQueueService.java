package com.bshauction.backend.service;


import com.bshauction.backend.entity.Bid;
import com.bshauction.backend.entity.Product;
import com.bshauction.backend.entity.User;
import com.bshauction.backend.repository.ProductRepository;
import com.bshauction.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import com.bshauction.backend.repository.BidRepository;

@Service
@RequiredArgsConstructor
public class MessageQueueService {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<BigDecimal>> bidRequests;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void enqueueBidRequest(String itemId, BigDecimal price) {
        ConcurrentLinkedQueue<BigDecimal> requests = bidRequests.computeIfAbsent(itemId, k -> new ConcurrentLinkedQueue<>());
        requests.offer(price);

//        System.out.println(requests);
//        System.out.println(requests.size());
//        if (requests.size() > 1) {
//            BigDecimal firstPrice = requests.poll();
//            System.out.println("Concurrent bid detected. First price: " + firstPrice);
//
//            // 나머지 요청에게는 해당 가격으로의 입찰이 불가능하다는 응답 전송
//        }else {
            User user = userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("err"));
            Product product = productRepository.findById(Long.valueOf(itemId)).orElseThrow(() -> new IllegalArgumentException("err"));

            Bid bid = Bid.builder()
                    .amount(price)
                    .user(user)
                    .product(product)
                    .build();

            bidRepository.save(bid);
//        }
    }
}
