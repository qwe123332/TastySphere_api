package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    //getAvatar
    public String getAvatar() {
        return avatar;
    }

    public User(String username, String password, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

/**
 * 定义 User 和 Role 实体之间的多对多关系。/**
  * 在更新实体之前更新 updatedTime 字段为当前时间。
  */
 @PreUpdate
 protected void onUpdate() {
     this.updatedTime = LocalDateTime.now();
 }
// * 连接表名为 user_roles，连接列为 user_id 和 role_id。
// */
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();

}
