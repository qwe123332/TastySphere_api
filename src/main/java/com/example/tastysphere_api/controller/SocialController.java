package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.tastysphere_api.service.UserService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.example.tastysphere_api.service.SensitiveWordService;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/social")
public class SocialController {
    @Autowired
    private SocialService socialService;

    @Autowired
    private UserService userService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/posts/{postId}/comments")
    public Comment createComment(
            @PathVariable Long postId,
            @RequestParam(required = false) Long parentId,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails user) {
        // 频率限制检查
        String key = "comment_limit:" + user.getUser().getId();
        String count = redisTemplate.opsForValue().get(key);
        if (count != null && Integer.parseInt(count) >= 10) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "评论次数超过限制，请稍后再试");
        }

        // 更新计数器
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);

        // 敏感词过滤
        String filteredContent = sensitiveWordService.filterContent(content);
        
        return socialService.createComment(postId, parentId, filteredContent, user.getUser());
    }

    @PostMapping("/posts/{postId}/like")
    public void toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        socialService.toggleLike(postId, user.getUser());
    }

    @PostMapping("/users/{userId}/follow")
    public void toggleFollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user) {
        User following = userService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        socialService.toggleFollow(user.getUser(), following);
    }

    @GetMapping("/posts/{postId}/comments")
    public Page<Comment> getPostComments(
            @PathVariable Long postId,
            Pageable pageable) {
        return socialService.getPostComments(postId, pageable);
    }

    @GetMapping("/comments/{commentId}/replies")
    public Page<Comment> getCommentReplies(
            @PathVariable Long commentId,
            Pageable pageable) {
        return socialService.getCommentReplies(commentId, pageable);
    }
}