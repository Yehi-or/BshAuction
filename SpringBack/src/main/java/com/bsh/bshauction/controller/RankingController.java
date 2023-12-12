package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.SearchRankingDTO;
import com.bsh.bshauction.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranking")
@CrossOrigin
@Slf4j
public class RankingController {
    private final RedisService redisService;

    @GetMapping("/searchRanking")
    public List<String> getRankingList() {
        return redisService.searchRankingList();
    }

}
