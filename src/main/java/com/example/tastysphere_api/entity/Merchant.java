package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.Timestamp;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//被标记的餐厅或商家，关联地理位置信息
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private String address;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String contactEmail;

    @Column(unique = true)
    private String businessLicense;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // PENDING, ACTIVE, SUSPENDED

    private Timestamp createdTime;
    private Timestamp updatedTime;

    public enum Status {
        PENDING, ACTIVE, SUSPENDED
    }
}