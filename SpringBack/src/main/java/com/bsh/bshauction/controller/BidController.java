package com.bsh.bshauction.controller;

import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
@Slf4j
public class BidController {
    private final JwtTokenProvider jwtTokenProvider;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JobLauncher jobLauncher;
    private final Job partitionLocalBatch;

    @PostMapping("/accessTokenTest")
    public void test(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Object userId) {
        String accessToken = jwtTokenProvider.extractAccessToken(authorizationHeader);
        if(jwtTokenProvider.validateToken(accessToken)) {
            Claims claims = jwtTokenProvider.parseClaims(accessToken);
            Long userIds = claims.get("userId", Long.class);
            String userRole = claims.get("role", String.class);
        }
    }

    @PostMapping("/bidExtension/{productId}")
    public void bidExtension(@PathVariable Long productId) {
        log.info("productId : {}", productId);

        try {
            Set<Object> rangeResult = redisTemplate.opsForZSet().range("product" + productId, 0, -1);

            if(rangeResult != null) {
                Long size = (long) rangeResult.size();

                log.info("product size : {}", size);

                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("productId", productId)
                        .addLong("alarmListSize", size)
                        .toJobParameters();

                log.info("partitioning data : {}", rangeResult);
                jobLauncher.run(partitionLocalBatch, jobParameters);
                log.info("Batch job started!");
            } else {
                log.info("rangeResult : null");
            }
        } catch (Exception e) {
            log.info("Error starting batch job: " + e.getMessage());
        }

    }

}
