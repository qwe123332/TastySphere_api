package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.entity.Notification;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Page<Notification> getNotifications(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return notificationService.getUserNotifications(user.getId(), pageable);
    }

    @PostMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user) {
        notificationService.markAsRead(notificationId, user.getId());
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(@AuthenticationPrincipal User user) {
        return notificationService.getUnreadCount(user.getId());
    }
} 