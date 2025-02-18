package com.example.tastysphere_api.service;

import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.entity.AuditLog;
import com.example.tastysphere_api.repository.PostRepository;
import com.example.tastysphere_api.repository.UserRepository;
import com.example.tastysphere_api.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.tastysphere_api.exception.BusinessException;
import com.example.tastysphere_api.exception.ResourceNotFoundException;

@Service
public class AdminService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private NotificationService notificationService;

    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalPosts", postRepository.count());
        stats.put("activeUsers", userRepository.countByActive(true));
        // 添加更多统计数据
        return stats;
    }

    public Map<String, Object> getDetailedStatistics() {
        Map<String, Object> stats = new HashMap<>();
        // 基础统计
        stats.put("totalUsers", userRepository.count());
        stats.put("totalPosts", postRepository.count());
        stats.put("activeUsers", userRepository.countByActive(true));
        
        // 今日统计
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0);
        Timestamp todayTimestamp = Timestamp.valueOf(today);
        stats.put("newUsersToday", userRepository.countByCreatedTimeAfter(todayTimestamp));
        stats.put("newPostsToday", postRepository.countByCreatedTimeAfter(todayTimestamp.toLocalDateTime()));
        
        // 审核统计
        stats.put("pendingAudit", postRepository.countByAuditedFalse());
        stats.put("approvedPosts", postRepository.countByAuditedTrueAndApprovedTrue());
        
        return stats;
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditPost(Long postId, boolean approved, User admin, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Audit reason cannot be empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        try {
            // 更新帖子状态
            post.setAudited(true);
            post.setApproved(approved);
            post.setAuditTime(LocalDateTime.now());
            postRepository.save(post);
            
            // 记录审核日志
            AuditLog auditLog = new AuditLog();
            auditLog.setAdmin(admin);
            auditLog.setActionType("POST_AUDIT");
            auditLog.setTargetId(postId);
            auditLog.setActionDetail("Post " + (approved ? "approved" : "rejected") + ". Reason: " + reason);
            auditLogRepository.save(auditLog);

            // 发送通知
            String message = approved ? 
                "您的帖子已通过审核" : 
                "您的帖子未通过审核，原因：" + reason;
            notificationService.sendNotification(post.getUser(), message, "POST_AUDIT");
            
            log.info("Successfully audited post {}, approved: {}", postId, approved);
        } catch (Exception e) {
            log.error("Error while auditing post {}: {}", postId, e.getMessage());
            throw new BusinessException("Failed to audit post: " + e.getMessage());
        }
    }

    public Page<User> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        try {
            user.setActive(active);
            userRepository.save(user);
            
            // 记录审计日志
            AuditLog auditLog = new AuditLog();
            auditLog.setActionType("USER_STATUS_UPDATE");
            auditLog.setTargetId(userId);
            auditLog.setActionDetail("User status updated to: " + (active ? "active" : "inactive"));
            auditLogRepository.save(auditLog);
            
            // 发送通知
            String message = active ? "您的账号已被激活" : "您的账号已被禁用";
            notificationService.sendNotification(user, message, "ACCOUNT_STATUS");
            
            log.info("Successfully updated user {} status to {}", userId, active);
        } catch (Exception e) {
            log.error("Error while updating user {} status: {}", userId, e.getMessage());
            throw new BusinessException("Failed to update user status: " + e.getMessage());
        }
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    // 添加系统监控方法
    public Map<String, Object> getSystemMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("cpu_usage", getCpuUsage());
            metrics.put("memory_usage", getMemoryUsage());
            metrics.put("disk_usage", getDiskUsage());
            return metrics;
        } catch (Exception e) {
            log.error("Error while getting system metrics: {}", e.getMessage());
            throw new BusinessException("Failed to get system metrics");
        }
    }

    private double getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return osBean.getSystemLoadAverage();
    }

    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private double getDiskUsage() {
        File file = new File("/");
        return (double) (file.getTotalSpace() - file.getFreeSpace()) / file.getTotalSpace() * 100;
    }

    public Page<Post> getPendingPosts(Pageable pageable) {
        return postRepository.findByAuditedFalse(pageable);
    }
} 