package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.security.Timestamp;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//关联的菜品或商品信息
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String category;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE; // ACTIVE, INACTIVE, OUT_OF_STOCK

    private Timestamp createdTime;
    private Timestamp updatedTime;

    public enum Status {
        ACTIVE, INACTIVE, OUT_OF_STOCK
    }
}