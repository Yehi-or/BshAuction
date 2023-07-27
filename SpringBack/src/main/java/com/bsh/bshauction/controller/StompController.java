package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.BidDto;
import com.bsh.bshauction.dto.BidMainReturnDTO;
import com.bsh.bshauction.dto.BidReturnDTO;
import com.bsh.bshauction.dto.SearchRankingDTO;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import com.bsh.bshauction.service.BidHistoryService;
import com.bsh.bshauction.service.RedisService;
import com.bsh.bshauction.service.StompService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class StompController {
    private final SimpMessagingTemplate template; //특정 Broker 로 메세지를 전달
    private final BidHistoryService bidHistoryService;
    private final StompService stompService;
    private final JwtTokenProvider jwtTokenProvider;

    @MessageMapping(value = "/product/bid/{productId}")
    public void enterProduct(@DestinationVariable Long productId, @Payload BidDto bidDto) {

        log.info("Message productId : {}, try price : {}, accessToken : {}", productId, bidDto.getBidPrice(), bidDto.getBidMessageAccessToken());

        String token = bidDto.getBidMessageAccessToken();

        if(StringUtils.hasText(token) && token.startsWith("Bearer")) {
            token = token.substring(7);
        }

        String returnTokenValidate = jwtTokenProvider.customValidateToken(token);

        if(token != null) {
            if(returnTokenValidate.equals("success")) {
                Claims claims = jwtTokenProvider.parseClaims(token);

                Object roleObject = claims.get("role");

                if(roleObject == null) {
                    template.convertAndSend("/sub/product/" + productId, "notHaveRole");
                    throw new RuntimeException("권한 정보가 없는 토큰입니다.");
                }

                if (!(roleObject instanceof String)) {
                    template.convertAndSend("/sub/product/" + productId, "notMatchRole");
                    throw new RuntimeException("올바르지 않은 권한 정보입니다.");
                }

                if(((String) roleObject).contains("ROLE_USER")) {
                    String returnType = bidHistoryService.bidAttempt(bidDto.getUserId(), bidDto.getBidPrice(), productId);
                    BidReturnDTO bidReturnDTO;

                    if(returnType.equals("success")) {
                        //경매 상세페이지 변경
                        bidReturnDTO = BidReturnDTO.builder()
                                .returnMessage(returnType)
                                .tryPrice(bidDto.getBidPrice())
                                .build();

                        template.convertAndSend("/sub/main/", stompService.returnMain(productId, bidDto.getBidPrice()));

                    } else {

                        bidReturnDTO = BidReturnDTO.builder()
                                .returnMessage(returnType)
                                .tryPrice(null)
                                .build();
                    }

                    //상품 식별자와 가격 추가해서 메인에서도 해당 상품의 가격이 변동 될 수 있게
                    template.convertAndSend("/sub/product/" + productId, bidReturnDTO);
                }else {
                    template.convertAndSend("/sub/product/" + productId, "notMatchROLE");
                }
            } else {
                template.convertAndSend("/sub/product/" + productId, returnTokenValidate);
            }
        } else {
            template.convertAndSend("/sub/product/" + productId, "requireLogin");
        }
    }


}
