package com.bsh.bshauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class BshAuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BshAuctionApplication.class, args);
    }

}
