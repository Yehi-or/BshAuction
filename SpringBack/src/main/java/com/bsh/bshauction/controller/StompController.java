package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.*;
import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import com.bsh.bshauction.service.BidHistoryService;
import com.bsh.bshauction.service.BidService;
import com.bsh.bshauction.service.StompService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final BidService bidService;

    @MessageMapping(value = "/product/bid/{productId}")
    public void enterProduct(@DestinationVariable Long productId, @Payload BidDto bidDto) {
        //이 메서드는 에러 핸들러로 에러 잡을 예정 아니면 그냥 try catch
        log.info("Message productId : {}, try price : {}, accessToken : {}", productId, bidDto.getBidPrice(), bidDto.getBidMessageAccessToken());

        String token = bidDto.getBidMessageAccessToken();

        if(StringUtils.hasText(token) && token.startsWith("Bearer")) {
            token = token.substring(7);
        }

        String returnTokenValidate = jwtTokenProvider.customValidateToken(token);

        if(token != null) {
            if(returnTokenValidate.equals("success")) {
                String roleObject = compareClaims(token, productId);

                BidReturnDTO bidReturnDTO;

                if(roleObject.contains("ROLE_USER")) {
                    ReturnBidAttemptDTO returnType = bidHistoryService.bidAttempt(bidDto.getUserId(), bidDto.getBidPrice(), productId);

                    if(returnType.getReturnMessage().equals("success")) {
                        //경매 상세페이지 변경
                        bidReturnDTO = BidReturnDTO.builder()
                                .returnBidAttemptDTO(returnType)
                                .tryPrice(bidDto.getBidPrice())
                                .build();

                        template.convertAndSend("/sub/main/", stompService.returnMain(productId, bidDto.getBidPrice()));

                    } else {

                        bidReturnDTO = BidReturnDTO.builder()
                                .returnBidAttemptDTO(returnType)
                                .tryPrice(null)
                                .build();
                    }

                    //상품 식별자와 가격 추가해서 메인에서도 해당 상품의 가격이 변동 될 수 있게
                    template.convertAndSend("/sub/product/" + productId, bidReturnDTO);
                }else {
                    ReturnBidAttemptDTO returnBidAttemptDTO = ReturnBidAttemptDTO.builder()
                            .returnMessage("notMatchROLE")
                            .build();

                    bidReturnDTO = BidReturnDTO.builder()
                            .returnBidAttemptDTO(returnBidAttemptDTO)
                            .tryPrice(null)
                            .build();

                    template.convertAndSend("/sub/product/" + productId, bidReturnDTO);
                }
            } else {
                ReturnBidAttemptDTO returnBidAttemptDTO = ReturnBidAttemptDTO.builder()
                        .returnMessage(returnTokenValidate)
                        .build();

                BidReturnDTO bidReturnDTO = BidReturnDTO.builder()
                        .returnBidAttemptDTO(returnBidAttemptDTO)
                        .tryPrice(null)
                        .build();

                template.convertAndSend("/sub/product/" + productId, bidReturnDTO);
            }
        } else {
            ReturnBidAttemptDTO returnBidAttemptDTO = ReturnBidAttemptDTO.builder()
                    .returnMessage("requireLogin")
                    .build();

            BidReturnDTO bidReturnDTO = BidReturnDTO.builder()
                    .returnBidAttemptDTO(returnBidAttemptDTO)
                    .tryPrice(null)
                    .build();

            template.convertAndSend("/sub/product/" + productId, bidReturnDTO);
        }
    }

    @MessageMapping(value = "/product/bidCancel/{productId}")
    public void bidCancel(@DestinationVariable Long productId, @Payload BidCancelDTO bidCancelDTO) {
        try {
            List<BidCancelInfoDTO> bidCancelInfoDTOS = bidCancelDTO.getSelectedBids();
            String token = bidCancelDTO.getBidMessageAccessToken();

            if(StringUtils.hasText(token) && token.startsWith("Bearer")) {
                token = token.substring(7);
            }

            String returnTokenValidate = jwtTokenProvider.customValidateToken(token);

            if(bidCancelInfoDTOS != null && returnTokenValidate.equals("success")) {
                String roleObject = compareClaims(token, productId);

                if(roleObject.contains("ROLE_USER")) {
                    //에러 발생해서 롤백될 수 도 있음....
                    ReturnBidDeleteDTO returnBidDeleteDTO = bidService.deleteBidHistoryAndUpdateProductPrice(bidCancelInfoDTOS, productId);

                    if(returnBidDeleteDTO.getReturnTypeString().equals("successUpdateAndDelete")) {
                        template.convertAndSend("/sub/main/", stompService.returnMain(productId, returnBidDeleteDTO.getUpdateBidPrice()));

                        template.convertAndSend("/sub/product/" + productId, BidCancelReturnDTO.builder()
                                        .bidCancelInfoDTOList(bidCancelInfoDTOS)
                                        .returnMessage("bidCancel")
                                        .build());
                    } else if(returnBidDeleteDTO.getReturnTypeString().equals("successDelete")){
                        template.convertAndSend("/sub/product/" + productId, BidCancelReturnDTO.builder()
                                .bidCancelInfoDTOList(bidCancelInfoDTOS)
                                .returnMessage("bidCancel")
                                .build());
                    } else {
                        template.convertAndSend("/sub/product/" + productId, BidCancelReturnDTO.builder()
                                .bidCancelInfoDTOList(null)
                                .returnMessage("errorForBidCancel")
                                .build());
                    }
                } else {
                    template.convertAndSend("/sub/product/" + productId, BidCancelReturnDTO.builder()
                            .bidCancelInfoDTOList(null)
                            .returnMessage("notMatchRole")
                            .build());
                }
            } else {
                template.convertAndSend("/sub/product/" + productId, BidCancelReturnDTO.builder()
                        .bidCancelInfoDTOList(null)
                        .returnMessage(returnTokenValidate)
                        .build());
            }
        } catch (Exception exception) {
            //예외처리
            //OptimisticLockException 이 올 예정
            System.out.println("error 발생 재시도 부탁");
        }
    }

    //토큰에서 userId 를 가져와서 해야하는지 고민중...
    private String compareClaims(String token, Long productId) {

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

        return (String) roleObject;
    }

    //중복되는 부분 메서드화 시킬 예정
}
