package com.example.tastysphere_api.dto;

import com.example.tastysphere_api.entity.Post.Visibility;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long postId;

    @NotNull
    private Long userId;

    private String title;

    private String username;

    private String userAvatar;

    @NotBlank(message = "Content cannot be empty")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<String> images;
    private List<CommentDTO> commentDTOs;
    @NotNull(message = "必须指定可见性")
    private Visibility visibility;

    private Integer likeCount = 0; // 点赞数
    private Integer commentCount = 0; // 评论数
    private LocalDateTime createdTime; // 创建时间
    private LocalDateTime updatedTime; // 更新时间
    private Boolean isLiked;  // 当前用户是否点赞
    private Boolean isMine;   // 是否是当前用户的帖子
    private Boolean audited; // 是否已审核
    private Boolean approved; // 是否已批准


}
