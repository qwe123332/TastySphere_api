package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_behaviors")
@Data
//记录用户浏览、点赞、搜索行为，用于推荐算法
public class UserBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "behavior_type")
    private String behaviorType; // VIEW, LIKE, COMMENT, SHARE

    @Column(name = "target_id")
    private Long targetId; // 内容ID（文章、餐厅等）

    @Column(name = "target_type")
    private String targetType; // POST, RESTAURANT, RECIPE

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "weight")
    private Double weight; // 行为权重
} 