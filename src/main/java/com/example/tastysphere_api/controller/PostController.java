package com.example.tastysphere_api.controller;


import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.repository.PostRepository;
import com.example.tastysphere_api.service.PostService;
import com.example.tastysphere_api.service.RecommendationService;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(postService.getPosts(user.getUser(), PageRequest.of(page, size)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        PostDTO postDTO = postService.getPost(postId);
        if (user != null) {
            Post post = postRepository.findById(postId).orElseThrow();
            recommendationService.recordUserView(user.getUserId(), post);
        }
        return ResponseEntity.ok(postDTO);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @Valid @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal CustomUserDetails user) throws BadRequestException {
        if (StringUtils.isBlank(postDTO.getTitle())) {
            throw new BadRequestException("标题不能为空");
        }
        return ResponseEntity.ok(postService.createPost(postDTO, user.getUser()));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(postService.updatePost(postId, postDTO, user.getUser()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        postService.deletePost(postId, user.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostDTO>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPosts(keyword, PageRequest.of(page, size)));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<PostDTO>> getRecommendedPosts(
            @AuthenticationPrincipal CustomUserDetails user) {
        if (user != null) {
            List<PostDTO> recommendedPosts = postService.getRecommendedPosts(user.getUser());
            return ResponseEntity.ok(recommendedPosts);
        }
        return ResponseEntity.ok(postService.getRecommendedPosts(null));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        postService.likePost(postId, user.getUser());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        postService.unlikePost(postId, user.getUser());
        return ResponseEntity.ok().build();
    }
}