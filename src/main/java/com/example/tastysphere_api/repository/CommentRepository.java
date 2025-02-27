package com.example.tastysphere_api.repository;

import com.example.tastysphere_api.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Page<Comment> findByParentCommentId(Long parentId, Pageable pageable);
    Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + :delta WHERE c.id = :commentId")
    void updateLikeCount(Long commentId, int delta);

    // findByParentCommentIdIn
    List<Comment> findByParentCommentIdIn(Collection<Long> parentIds);

    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);

    List<Comment> findByParentCommentId(Long parentCommentId);
}