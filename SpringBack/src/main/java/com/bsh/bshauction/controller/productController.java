package com.bsh.bshauction.controller;

import com.bsh.bshauction.dto.BidEndTimeDTO;
import com.bsh.bshauction.dto.BidListDTO;
import com.bsh.bshauction.dto.ProductDTO;
import com.bsh.bshauction.dto.ProductListDTO;
import com.bsh.bshauction.entity.Product;
import com.bsh.bshauction.repository.BidRepository;
import com.bsh.bshauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
@Slf4j
public class productController {
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

    @GetMapping("/list")
    public ResponseEntity<List<ProductListDTO>> productList() {
        List<Product> productList = productRepository.findAll();
        List<ProductListDTO> productListDTO = new ArrayList<>();

        for(Product product: productList) {
            ProductListDTO dto = ProductListDTO.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productPrice(product.getPrice())
                    .build();
            productListDTO.add(dto);
        }

        return ResponseEntity.ok().body(productListDTO);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> detailProductInfo(@PathVariable Long productId) {

        log.info("---productId : {}", productId);
        Optional<Product> product = productRepository.findById(productId);

        if(product.isPresent()) {
            List<BidListDTO> bidList = bidRepository.findByProductOrderByAmountAsc(product.get());

            ProductDTO productDTO = ProductDTO.builder()
                    .productName(product.get().getProductName())
                    .price(product.get().getPrice())
                    .bidList(bidList)
                    .build();

            return ResponseEntity.ok().body(productDTO);
        }
        return null;
    }

    @GetMapping("/bidEndTime/{productId}")
    public ResponseEntity<BidEndTimeDTO> getBidEndTime(@PathVariable Long productId) {

        log.info("---productId : {}", productId);
        Optional<Product> product = productRepository.findById(productId);

        if(product.isPresent()) {
            LocalDateTime bidEndTime = product.get().getFinishAt();
            LocalDateTime currentTime = LocalDateTime.now();

            BidEndTimeDTO bidEndTimeDTO = BidEndTimeDTO.builder()
                    .bidEndTime(bidEndTime)
                    .currentTime(currentTime)
                    .build();

            return ResponseEntity.ok().body(bidEndTimeDTO);
        }
        return ResponseEntity.status(404).body(BidEndTimeDTO.builder().build());
    }

}
