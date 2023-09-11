package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.*;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import com.bsh.bshauction.repository.ProductRepository;
import com.bsh.bshauction.service.BidHistoryService;
import com.bsh.bshauction.service.BidService;
import com.bsh.bshauction.service.StompService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class StompController {
//    private final SimpMessagingTemplate template; //특정 Broker 로 메세지를 전달
    private final BidHistoryService bidHistoryService;
    private final StompService stompService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BidService bidService;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @MessageMapping(value = "product.bid.{productId}")
    public void enterProduct(@DestinationVariable Long productId, @Payload BidDto bidDto) {
        try {
            log.info("Message productId : {}, try price : {}, accessToken : {}", productId, bidDto.getBidPrice(), bidDto.getBidMessageAccessToken());

            String token = bidDto.getBidMessageAccessToken();

            if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
                token = token.substring(7);
            }

            String returnTokenValidate = jwtTokenProvider.customValidateToken(token);

            if (token != null) {
                if (returnTokenValidate.equals("success")) {

                    String roleObject = compareClaims(token, productId);
                    BidReturnDTO bidReturnDTO;

                    if (roleObject.contains("ROLE_USER")) {
                        ReturnBidAttemptDTO returnType = bidHistoryService.bidAttempt(bidDto.getUserId(), bidDto.getBidPrice(), productId);

                        if (returnType.getReturnMessage().equals("success")) {
                            //redis 로 중복값 없이 해당 상품에 입찰한 유저 정보 저장.
                            redisTemplate.opsForZSet().add("product" + productId, bidDto.getUserId(), 0);

                            //경매 상세페이지 변경
                            bidReturnDTO = BidReturnDTO.builder()
                                    .returnBidAttemptDTO(returnType)
                                    .tryPrice(bidDto.getBidPrice())
                                    .userId(bidDto.getUserId())
                                    .build();
                            rabbitTemplate.convertAndSend("main.Exchange", "main", stompService.returnMain(productId, bidDto.getBidPrice()));

                        } else {

                            bidReturnDTO = BidReturnDTO.builder()
                                    .returnBidAttemptDTO(returnType)
                                    .tryPrice(null)
                                    .userId(bidDto.getUserId())
                                    .build();
                        }

                        //상품 식별자와 가격 추가해서 메인에서도 해당 상품의 가격이 변동 될 수 있게
                        bidReturnDTO.getReturnBidAttemptDTO().setEndTime(null);
                        rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, bidReturnDTO);

                    } else {
                        ReturnBidAttemptDTO returnBidAttemptDTO = ReturnBidAttemptDTO.builder()
                                .returnMessage("notMatchROLE")
                                .build();

                        bidReturnDTO = BidReturnDTO.builder()
                                .returnBidAttemptDTO(returnBidAttemptDTO)
                                .tryPrice(null)
                                .userId(bidDto.getUserId())
                                .build();

                        bidReturnDTO.getReturnBidAttemptDTO().setEndTime(null);
                        rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, bidReturnDTO);
                    }
                } else {
                    ReturnBidAttemptDTO returnBidAttemptDTO = ReturnBidAttemptDTO.builder()
                            .returnMessage(returnTokenValidate)
                            .build();

                    BidReturnDTO bidReturnDTO = BidReturnDTO.builder()
                            .returnBidAttemptDTO(returnBidAttemptDTO)
                            .tryPrice(null)
                            .userId(bidDto.getUserId())
                            .build();

                    bidReturnDTO.getReturnBidAttemptDTO().setEndTime(null);
                    rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, bidReturnDTO);
                }
            } else {
                ReturnBidAttemptDTO returnBidAttemptDTO = ReturnBidAttemptDTO.builder()
                        .returnMessage("requireLogin")
                        .build();

                BidReturnDTO bidReturnDTO = BidReturnDTO.builder()
                        .returnBidAttemptDTO(returnBidAttemptDTO)
                        .tryPrice(null)
                        .userId(bidDto.getUserId())
                        .build();

                bidReturnDTO.getReturnBidAttemptDTO().setEndTime(null);
                rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, bidReturnDTO);
            }
        } catch (Exception e) {
            //예외처리
            //OptimisticLockException 이 올 예정
            log.info("error" + e);
        }
    }

    @MessageMapping(value = "/product.bidCancel.{productId}")
    public void bidCancel(@DestinationVariable Long productId, @Payload BidCancelDTO bidCancelDTO) {
        try {
            List<BidCancelInfoDTO> bidCancelInfoDTOS = bidCancelDTO.getSelectedBids();
            String token = bidCancelDTO.getBidMessageAccessToken();

            if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
                token = token.substring(7);
            }

            String returnTokenValidate = jwtTokenProvider.customValidateToken(token);

            if (bidCancelInfoDTOS != null && returnTokenValidate.equals("success")) {
                String roleObject = compareClaims(token, productId);

                if (roleObject.contains("ROLE_USER")) {
                    //에러 발생해서 롤백될 수 도 있음....
                    ReturnBidDeleteDTO returnBidDeleteDTO = bidService.deleteBidHistoryAndUpdateProductPrice(bidCancelInfoDTOS, productId);

                    //!!!!!!!!!!!--추가할부분 : userID를 가지고 와서 deleteRow 의 갯수와 Bid 에서 userId 로 조회한 입찰 갯수를 비교해서 갯수가 똑같으면 redis 값 삭제-- 기본적으로 바디에 해당 userId 가 존재하고 있음
                    //그 userId를 이용해서 productRepository 를 조회해서 해당 사용자의 bid try 개수를 가져와 deleteRow 와 비교 후에 redis 값 삭제할 예정

                    if (returnBidDeleteDTO.getReturnTypeString().equals("successUpdateAndDelete")) {
                        rabbitTemplate.convertAndSend("main.Exchange", "main", stompService.returnMain(productId, returnBidDeleteDTO.getUpdateBidPrice()));

                        rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, BidCancelReturnDTO.builder()
                                .bidCancelInfoDTOList(bidCancelInfoDTOS)
                                .returnMessage("bidCancel")
                                .build());
                    } else if (returnBidDeleteDTO.getReturnTypeString().equals("successDelete")) {
                        rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, BidCancelReturnDTO.builder()
                                .bidCancelInfoDTOList(bidCancelInfoDTOS)
                                .returnMessage("bidCancel")
                                .build());
                    } else {
                        rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, BidCancelReturnDTO.builder()
                                .bidCancelInfoDTOList(null)
                                .returnMessage("errorForBidCancel")
                                .build());
                    }
                } else {
                    rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, BidCancelReturnDTO.builder()
                            .bidCancelInfoDTOList(null)
                            .returnMessage("notMatchRole")
                            .build());
                }
            } else {
                rabbitTemplate.convertAndSend("amq.topic", "productId." + productId, BidCancelReturnDTO.builder()
                        .bidCancelInfoDTOList(null)
                        .returnMessage(returnTokenValidate)
                        .build());
            }
        } catch (Exception exception) {
            //예외처리
            //OptimisticLockException 이 올 예정
            System.out.println("bid_cancel 에러 발생 재시도 부탁");
        }
    }

    //토큰에서 userId 를 가져와서 해야하는지 고민중...
    private String compareClaims(String token, Long productId) {

        Claims claims = jwtTokenProvider.parseClaims(token);

        Object roleObject = claims.get("role");

        if (roleObject == null) {
//            template.convertAndSend("/sub/product/" + productId, "notHaveRole");
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        if (!(roleObject instanceof String)) {
//            template.convertAndSend("/sub/product/" + productId, "notMatchRole");
            throw new RuntimeException("올바르지 않은 권한 정보입니다.");
        }

        return (String) roleObject;
    }

    @RabbitListener(queues = "bshAuction_bid")
    private void consumer() {
        System.out.println("큐에 등록 되셨습니다.");
    }
}
