package com.example.tastysphere_api.repository;
import com.example.tastysphere_api.entity.Follow;
import com.example.tastysphere_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    @Transactional
    void deleteByFollowerAndFollowing(User follower, User following);
    
    // 获取用户的关注列表
    Page<Follow> findByFollower(User follower, Pageable pageable);
    
    // 获取用户的粉丝列表
    Page<Follow> findByFollowing(User following, Pageable pageable);
    
    // 获取用户的所有关注ID
    List<Long> findFollowingIdsByFollower(User follower);
    
    // 获取关注数量
    long countByFollower(User follower);
    
    // 获取粉丝数量
    long countByFollowing(User following);
}