package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_tags")
@Data
// 用户标签，用于推荐系统
//用户兴趣标签（如#川菜爱好者），用于推荐
public class UserTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "tag_type")
    private String tagType; // CUISINE, TASTE, DIET_PREFERENCE

    @Column(name = "weight")
    private Double weight; // 标签权重
} 