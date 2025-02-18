package com.example.tastysphere_api.service;

import com.example.tastysphere_api.entity.*;
import com.example.tastysphere_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialService {
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private NotificationService notificationService;

    // 评论相关方法
    @Transactional
    public Comment createComment(Long postId, Long parentId, String content, User user) {
        Comment comment = new Comment();
        comment.setPost(postRepository.getReferenceById(postId));
        comment.setUser(user);
        comment.setContent(content);
        if (parentId != null) {
            comment.setParentComment(commentRepository.getReferenceById(parentId));
        }
        
        // 添加通知
        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId).orElseThrow();
            notificationService.createNotification(
                parentComment.getUser(),
                user.getUsername() + " 回复了你的评论",
                "COMMENT",
                comment.getId()
            );
        }
        
        return commentRepository.save(comment);
    }

    // 点赞相关方法
    @Transactional
    public void toggleLike(Long postId, User user) {
        Post post = postRepository.getReferenceById(postId);
        if (likeRepository.existsByPostAndUser(post, user)) {
            likeRepository.deleteByPostAndUser(post, user);
            postRepository.updateLikeCount(postId, -1);
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            postRepository.updateLikeCount(postId, 1);
        }
        
        // 添加通知
        notificationService.createNotification(
            post.getUser(),
            user.getUsername() + " 赞了你的帖子",
            "LIKE",
            postId
        );
    }

    // 关注相关方法
    @Transactional
    public void toggleFollow(User follower, User following) {
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            followRepository.deleteByFollowerAndFollowing(follower, following);
        } else {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            followRepository.save(follow);
        }
        
        // 添加通知
        notificationService.createNotification(
            following,
            follower.getUsername() + " 关注了你",
            "FOLLOW",
            follower.getId()
        );
    }

    // 获取评论列表
    public Page<Comment> getPostComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    // 获取回复列表
    public Page<Comment> getCommentReplies(Long commentId, Pageable pageable) {
        return commentRepository.findByParentCommentId(commentId, pageable);
    }
}