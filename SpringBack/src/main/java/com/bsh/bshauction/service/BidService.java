package com.bsh.bshauction.service;

import com.bsh.bshauction.dto.BidCancelInfoDTO;
import com.bsh.bshauction.dto.ReturnBidDeleteDTO;
import com.bsh.bshauction.dto.ReturnUpdateProductBidDTO;
import com.bsh.bshauction.entity.Product;
import com.bsh.bshauction.repository.BidHistoryRepository;
import com.bsh.bshauction.repository.BidRepository;
import com.bsh.bshauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class BidService {
    private final BidHistoryRepository bidHistoryRepository;
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    RedisTemplate<String, String> redisTemplate;

    public boolean deleteBidHistory(BidCancelInfoDTO bidCancelInfoDTO, Long productId) {
        Long userId = bidCancelInfoDTO.getUserId();
        BigDecimal bidProductPrice = bidCancelInfoDTO.getBidPrice();

        Long deleteRows = bidHistoryRepository.deleteBidHistoryUserIdAndProductId(userId, productId, bidProductPrice);

        if (deleteRows > 0) {
            Long deleteBidRows = bidRepository.deleteBidUserIdAndProductId(userId, productId, bidProductPrice);

            return deleteBidRows > 0;
        }

        return false;
    }

    public ReturnUpdateProductBidDTO updateProductPriceCurrently(Long productId, BigDecimal mostHighBidPrice) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if(product.getPrice().compareTo(mostHighBidPrice) > 0) {

            return ReturnUpdateProductBidDTO.builder()
                    .returnMostPrice(null)
                    .returnBool(false)
                    .build();

        } else if(product.getPrice().compareTo(mostHighBidPrice) == 0) {

            Product updatedProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if(product.getVersion().equals(updatedProduct.getVersion())) {
                BigDecimal mostPrice = bidRepository.findMaxPrice(product);
                updatedProduct.setPrice(mostPrice);
                productRepository.save(updatedProduct);

                return ReturnUpdateProductBidDTO.builder()
                        .returnMostPrice(mostPrice)
                        .returnBool(true)
                        .build();
            } else {
                throw new OptimisticLockException("Concurrent update detected for product with ID: " + productId);
            }
        }

        return ReturnUpdateProductBidDTO.builder()
                .returnMostPrice(null)
                .returnBool(false)
                .build();
    }

    @Transactional
    public ReturnBidDeleteDTO deleteBidHistoryAndUpdateProductPrice(List<BidCancelInfoDTO> bidCancelInfoDTOS, Long productId) {

        boolean isDelete = false;
        int deleteCnt = 0;
        BigDecimal mostHighBidPrice = bidCancelInfoDTOS.get(0).getBidPrice();

        for (int i = 0; i < bidCancelInfoDTOS.size(); i++) {
            BidCancelInfoDTO bidCancelInfoDTO = bidCancelInfoDTOS.get(i);

            if (i >= 1) {
                BigDecimal tempHighBidPrice = bidCancelInfoDTO.getBidPrice();
                if (tempHighBidPrice.compareTo(mostHighBidPrice) > 0) {
                    mostHighBidPrice = tempHighBidPrice;
                }
            }

            isDelete = deleteBidHistory(bidCancelInfoDTO, productId);
            deleteCnt++;
        }

        ReturnUpdateProductBidDTO returnUpdateProductBidDTO = updateProductPriceCurrently(productId, mostHighBidPrice);

        if(isDelete && returnUpdateProductBidDTO.isReturnBool()) {
            return ReturnBidDeleteDTO.builder()
                    .updateBidPrice(returnUpdateProductBidDTO.getReturnMostPrice())
                    .returnTypeString("successUpdateAndDelete")
                    .deleteCnt(deleteCnt)
                    .build();
        } else if(isDelete){
            return ReturnBidDeleteDTO.builder()
                    .updateBidPrice(null)
                    .returnTypeString("successDelete")
                    .deleteCnt(deleteCnt)
                    .build();
        } else {
            return ReturnBidDeleteDTO.builder()
                    .updateBidPrice(null)
                    .returnTypeString("fail")
                    .deleteCnt(deleteCnt)
                    .build();
        }
    }

}