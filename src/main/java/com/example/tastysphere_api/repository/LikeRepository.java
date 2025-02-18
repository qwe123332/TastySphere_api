package com.example.tastysphere_api.repository;
import com.example.tastysphere_api.entity.Like;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostAndUser(Post post, User user);
    
    @Transactional
    void deleteByPostAndUser(Post post, User user);
    
    long countByPostId(Long postId);
    
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}