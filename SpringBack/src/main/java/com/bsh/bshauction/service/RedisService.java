package com.bsh.bshauction.service;

import com.bsh.bshauction.dto.SearchRankingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String SEARCH_WEIGHT_PREFIX = "search_weight:";
    private double totalSearchFrequency = 0;
    private final RabbitTemplate rabbitTemplate;

    //검색 메소드
    @Async
    public void doSearch(String keyword) {

        double weight = findSearchKeywordWeight(keyword);

        log.info("weight : {}", weight);
        redisTemplate.opsForZSet().incrementScore("search_ranking", keyword, weight);

        //1시간 동안 특정 검색어가 얼마나 증가한지 확인
        redisTemplate.opsForZSet().incrementScore("search_keyword_forOneHour", keyword, 1);
        totalSearchFrequency++;
    }

    // 1시간 마다 평균 검색 횟수를 구하는 메서드 가중치를 주고 싶은데 후에 추가할 예정 2023-07-24
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void getAverageSearchFrequencyForOneHour() {
        log.info("--Start Scheduled--");
        getAverageSearchFrequencyForOneHours();
        log.info("--End Scheduled--");
    }

    @Scheduled(fixedRate = 5 * 6000)
    public void getSearchRanking() {
        log.info("--update search ranking--");
        List<String> rankingList = searchRankingList();
        if(rankingList != null) {
            rabbitTemplate.convertAndSend("message_exchange", "delivery_message", rankingList);
        }
    }

    private Set<ZSetOperations.TypedTuple<String>> getAllSearchKeywordForOneHour(String keyValue) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(keyValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    private Double findSearchKeywordWeight(String keyword) {

        String weightKey = SEARCH_WEIGHT_PREFIX + keyword;
        String weightString = redisTemplate.opsForValue().get(weightKey);

        if(weightString != null) {
            return Double.parseDouble(weightString);
        }

        return 1.0;
    }

    //후에 Lock 을 적용시켜서 동시성 해결해야함 totalSearchFrequency++ 부분과 redisTemplate 부분 동시성 문제가 발생함 Redis 의 pub-sub Message 락을 걸 예정
    @Async
    public void getAverageSearchFrequencyForOneHours() {
        String searchFrequencyWord = "search_keyword_forOneHour";
        Set<ZSetOperations.TypedTuple<String>> searchKeywords = getAllSearchKeywordForOneHour(searchFrequencyWord);

        int numberOfSearchKeywords = searchKeywords.size();

        log.info("searchKeywords_size : {}", numberOfSearchKeywords);

        if(numberOfSearchKeywords > 0) {
            String keyword;
            Double searchFrequency;
            double averageSearchFrequency = totalSearchFrequency / numberOfSearchKeywords;

            log.info("averageSearchFrequency : {}", averageSearchFrequency);

            if(averageSearchFrequency > 0) {

                for (ZSetOperations.TypedTuple<String> searchKeyword : searchKeywords) {
                    keyword = searchKeyword.getValue();
                    searchFrequency = searchKeyword.getScore();
                    double weight;

                    if (searchFrequency != null && searchFrequency != 0) {
                        if(searchFrequency > averageSearchFrequency) {
                            //검색 빈도에 따라 가중치를 계산
                            weight = searchFrequency / averageSearchFrequency;
                        } else {
                            weight = 1.0;
                        }

                        // 가중치를 새로운 키에 저장 (search_weight:{검색어})
                        String weightKey = SEARCH_WEIGHT_PREFIX + keyword;
                        redisTemplate.opsForValue().set(weightKey, String.valueOf(weight));
                    }
                }
            }
        }

        redisTemplate.opsForZSet().removeRange("search_keyword_forOneHour", 0, -1);
        totalSearchFrequency = 0;
    }


    public List<String> searchRankingList() {
        String key = "search_ranking";
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 9);
        if(typedTuples != null) {
            return typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).collect(Collectors.toList());
        }
        return null;
    }

}
