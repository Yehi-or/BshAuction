package com.bshauction.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class currentHashMapConfig {
    @Bean
    public ConcurrentHashMap<String, ConcurrentLinkedQueue<BigDecimal>> bidRequests() {
        return new ConcurrentHashMap<>();
    }
}
