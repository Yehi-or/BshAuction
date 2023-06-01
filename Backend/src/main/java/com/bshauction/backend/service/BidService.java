package com.bshauction.backend.service;

import com.bshauction.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidService {
    private final ProductRepository productRepository;
    private final BidHistoryService bidHistoryService;
}
