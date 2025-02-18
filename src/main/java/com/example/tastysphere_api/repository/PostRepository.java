package com.example.tastysphere_api.repository;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.Post.Visibility;
import com.example.tastysphere_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 获取用户可见的帖子
    @Query("SELECT p FROM Post p WHERE p.visibility = 'PUBLIC' OR " +
            "(p.visibility = 'FRIENDS_ONLY' AND p.user.id IN :friendIds) OR " +
            "(p.user.id = :userId)")
    Page<Post> findVisiblePosts(Long userId, List<Long> friendIds, Pageable pageable);

    // 获取某个用户的所有帖子
    Page<Post> findById(Long Id, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.audited = true")
    Page<Post> findAllAudited(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.audited = true AND p.approved = true")
    Page<Post> findAllAuditedAndApproved(Pageable pageable);

    // 更新点赞数
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + :delta WHERE p.id = :postId")
    void updateLikeCount(Long postId, int delta);

    // 更新评论数
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + :delta WHERE p.id = :postId")
    void updateCommentCount(Long postId, int delta);


    // 根据用户获取帖子
    Page<Post> findByUser(User user, Pageable pageable);

    // 根据可见性筛选
    Page<Post> findByVisibility(Visibility visibility, Pageable pageable);

    // 关键词搜索
    Page<Post> findByContentContaining(String keyword, Pageable pageable);

    long countByCreatedTimeAfter(LocalDateTime createdTime);
    long countByAuditedFalse();
    long countByAuditedTrueAndApprovedTrue();

    // 添加审核状态相关的查询方法
    Page<Post> findByAuditedTrueAndApprovedTrue(Pageable pageable);
    
    // 获取待审核的帖子
    Page<Post> findByAuditedFalse(Pageable pageable);
    
    // 获取某个用户的已审核通过的帖子
    Page<Post> findByUserAndAuditedTrueAndApprovedTrue(User user, Pageable pageable);

    Page<Post> findByUserIdInAndApprovedTrue(List<Long> userIds, Pageable pageable);

    Page<Post> findByAuditedTrue(Pageable pageable);

    List<Post> findTop10ByAuditedTrueAndApprovedTrueOrderByCreatedTimeDesc();
}
