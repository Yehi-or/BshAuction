package com.bsh.bshauction.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "finish_at", nullable = false)
    private LocalDateTime finishAt;

    @Column
    private Integer bidTime;

    @Column
    private boolean isFinishBid;

    @Builder
    public Product(Long productId, String productName, BigDecimal price, List<Bid> bids, User seller, LocalDateTime finishAt) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.bids = bids;
        this.seller = seller;
        this.finishAt = finishAt;
    }
}
