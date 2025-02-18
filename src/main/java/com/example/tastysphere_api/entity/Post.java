package com.example.tastysphere_api.entity;

import com.example.tastysphere_api.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "posts",
        indexes = {
                @Index(name = "idx_user_created", columnList = "user_id, created_time"),
                @Index(name = "idx_visibility", columnList = "visibility")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String title;

    @Convert(converter = StringListConverter.class) // 使用 JSON 转换器
    @Column(columnDefinition = "TEXT") // 兼容数据库，不强依赖 JSON 类型
    private List<String> images= new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments; // ✅ 确保 Post 里有 comments 字段

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedTime;

    @Column(name = "is_audited")
    private boolean audited = false;

    @Column(name = "is_approved")
    private boolean approved = false;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column
    private String category;  // 添加分类字段

    public enum Visibility {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }
}
