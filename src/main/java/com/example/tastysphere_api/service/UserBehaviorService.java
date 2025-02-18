package com.example.tastysphere_api.service;

import com.example.tastysphere_api.entity.UserBehavior;
import com.example.tastysphere_api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.example.tastysphere_api.repository.UserBehaviorRepository;

@Service
public class UserBehaviorService {
    @Autowired
    private UserBehaviorRepository behaviorRepository;

    public void recordBehavior(User user, String behaviorType, 
                             Long targetId, String targetType, Double weight) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUser(user);
        behavior.setBehaviorType(behaviorType);
        behavior.setTargetId(targetId);
        behavior.setTargetType(targetType);
        behavior.setWeight(weight);
        behaviorRepository.save(behavior);
    }

    public List<UserBehavior> getUserBehaviors(Long userId, String behaviorType) {
        return behaviorRepository.findByUserIdAndBehaviorType(userId, behaviorType);
    }
} 