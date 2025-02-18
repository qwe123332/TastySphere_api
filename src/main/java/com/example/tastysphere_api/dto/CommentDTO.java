package com.example.tastysphere_api.dto;

import lombok.Data;

@Data

public class CommentDTO {
    private Long id;
    private String username;
    private String userAvatar;
    private String content;
    private Long userId;
    private Integer likeCount;
    private Long dishId;
    private String createdAt;
    private String updatedAt;


}
