package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//订单记录，关联用户、商家、订单详情
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // PENDING, CONFIRMED, CANCELLED, COMPLETED

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID; // UNPAID, PAID, REFUNDED

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.ONLINE; // CASH, CARD, ONLINE

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING; // PENDING, IN_PROGRESS, DELIVERED

    @Column(nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(nullable = false)
    private String contactPhone;

    private Timestamp createdTime;
    private Timestamp updatedTime;

    public enum Status {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
    // 🔹 在这里加入和 OrderDetail 的一对多关系
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public enum PaymentStatus {
        UNPAID, PAID, REFUNDED
    }

    public enum PaymentMethod {
        CASH, CARD, ONLINE
    }

    public enum DeliveryStatus {
        PENDING, IN_PROGRESS, DELIVERED
    }
}