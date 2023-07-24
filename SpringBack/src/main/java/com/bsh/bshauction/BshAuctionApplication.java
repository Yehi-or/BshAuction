package com.bsh.bshauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BshAuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BshAuctionApplication.class, args);
    }

}
