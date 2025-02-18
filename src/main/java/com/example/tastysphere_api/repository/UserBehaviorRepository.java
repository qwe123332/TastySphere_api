package com.example.tastysphere_api.repository;

import com.example.tastysphere_api.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    List<UserBehavior> findByUserIdAndBehaviorType(Long userId, String behaviorType);
} 