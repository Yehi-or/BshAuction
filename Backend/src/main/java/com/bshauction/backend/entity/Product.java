package com.bshauction.backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "products")
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Bid> bids;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Builder
    public Product(Long productId, String productName, BigDecimal price, List<Bid> bids, User seller) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.bids = bids;
        this.seller = seller;
    }
}
