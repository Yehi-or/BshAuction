package com.bsh.bshauction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String SEARCH_WEIGHT_PREFIX = "search_weight:";

    public void doSearch(String keyword) {

        String weightKey = SEARCH_WEIGHT_PREFIX + keyword;
        String weightString = redisTemplate.opsForValue().get(weightKey);

        if (weightString != null) {
            double weight = Double.parseDouble(weightString);
            redisTemplate.opsForZSet().incrementScore("search_ranking", keyword, weight);
        } else {
            redisTemplate.opsForZSet().incrementScore("search_ranking", keyword, 1);
        }
    }

    // 1시간 마다 평균 검색 횟수를 구하는 메서드
    @Scheduled(fixedRate = 60 * 60 * 1000)
    private void getAverageSearchFrequencyForOneHour() {

        log.info("--Scheduled--");

        long currentTime = Instant.now().getEpochSecond();
        long oneHourAgo = currentTime - (60 * 60);

        Set<ZSetOperations.TypedTuple<String>> searchKeywords = getAllSearchKeywordForOneHour(currentTime, oneHourAgo);
        double totalSearchFrequency = 0;

        for (ZSetOperations.TypedTuple<String> searchKeyword : searchKeywords) {
            if (searchKeyword != null) {
                Double searchFrequency = searchKeyword.getScore();
                if(searchFrequency != null) {
                    totalSearchFrequency += searchFrequency;
                }
            }
        }

        int numberOfSearchKeywords = searchKeywords.size();

        if (numberOfSearchKeywords > 0) {
            // 1시간 동안 검색된 평균 빈도수를 구함
            double averageSearchFrequency = totalSearchFrequency / numberOfSearchKeywords;
            String keyword;
            Double searchFrequency;

            // 검색어의 가중치를 계산하고 적용함
            for (ZSetOperations.TypedTuple<String> searchKeyword : searchKeywords) {
                if (searchKeyword != null) {
                    keyword = searchKeyword.getValue();
                    searchFrequency = searchKeyword.getScore();

                    if (searchFrequency != null) {
                        // 검색 빈도에 따라 가중치를 계산하여 적용
                        double weight = searchFrequency / averageSearchFrequency;

                        // 가중치를 새로운 키에 저장 (search_weight:{검색어})
                        String weightKey = SEARCH_WEIGHT_PREFIX + keyword;
                        redisTemplate.opsForValue().set(weightKey, String.valueOf(weight));
                    }
                }
            }
        }
    }

    private Set<ZSetOperations.TypedTuple<String>> getAllSearchKeywordForOneHour(long currentTime, long oneHourAgo) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores("search_ranking", oneHourAgo, currentTime);
    }

}
