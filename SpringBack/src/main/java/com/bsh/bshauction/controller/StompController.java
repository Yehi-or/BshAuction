package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.BidDto;
import com.bsh.bshauction.dto.BidMainReturnDTO;
import com.bsh.bshauction.dto.BidReturnDTO;
import com.bsh.bshauction.service.BidHistoryService;
import com.bsh.bshauction.service.StompService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class StompController {
    private final SimpMessagingTemplate template; //특정 Broker 로 메세지를 전달
    private final BidHistoryService bidHistoryService;
    private final StompService stompService;

    // /pub 과 맵핑
    @MessageMapping(value = "/main")
    public void enterMain() {
        template.convertAndSend("/sub/main");
    }

    @MessageMapping(value = "/product/bid/{productId}")
    public void enterProduct(@DestinationVariable Long productId, @Payload BidDto bidDto) {
        log.info("Message productId : {}, try price : {}", productId, bidDto.getBidPrice());

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


        template.convertAndSend("/sub/product/" + productId, bidReturnDTO);
        //상품 식별자와 가격 추가해서 메인에서도 해당 상품의 가격이 변동 될 수 있게
    }


}
