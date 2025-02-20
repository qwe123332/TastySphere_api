package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
//用户基础信息，包含社交属性（粉丝数、关注数）和发布的帖子数
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    //avatar
    @Column(nullable = false, unique = true)
    private String avatar;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private LocalDateTime updatedTime;
    @Column(name  = "status")
    private String status;

    @Column(name = "is_active")
    private boolean active = true;

// * 连接表名为 user_roles，连接列为 user_id 和 role_id。
// */
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
)
private List<Role> roles = new ArrayList<>();

/**
 * 定义 User 和 Role 实体之间的多对多关系。/**
  * 在更新实体之前更新 updatedTime 字段为当前时间。
  */
 @PreUpdate
 protected void onUpdate() {
     this.updatedTime = LocalDateTime.now();
 }
    public User(String username, String password, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }




}
