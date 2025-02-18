package com.example.tastysphere_api.service;

import com.example.tastysphere_api.entity.Notification;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void createNotification(User user, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notificationRepository.save(notification);
    }

    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser().getId().equals(userId)) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void sendNotification(User user, String message, String type) {
        String key = "notifications:" + user.getId();
        String notification = String.format("{\"type\":\"%s\",\"message\":\"%s\",\"time\":\"%s\"}", 
            type, message, java.time.LocalDateTime.now());
            
        // 使用 Redis 列表存储通知
        redisTemplate.opsForList().leftPush(key, notification);
        // 限制通知数量，保留最新的100条
        redisTemplate.opsForList().trim(key, 0, 99);
    }

    public void sendSystemNotification(String message) {
        // 系统级通知，存储在特定的系统通知队列中
        String key = "notifications:system";
        String notification = String.format("{\"type\":\"SYSTEM\",\"message\":\"%s\",\"time\":\"%s\"}", 
            message, java.time.LocalDateTime.now());
        redisTemplate.opsForList().leftPush(key, notification);
    }
} 