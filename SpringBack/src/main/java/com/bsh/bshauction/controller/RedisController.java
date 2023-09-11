package com.bsh.bshauction.controller;

import com.bsh.bshauction.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class RedisController {
    private final RedisService redisService;
    @GetMapping("/{keyword}")
    public void doSearch(@PathVariable String keyword) {
        redisService.doSearch(keyword);
    }

}
