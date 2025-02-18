package com.example.tastysphere_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(name = "action_type")
    private String actionType; // POST_AUDIT, USER_BAN, etc.

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "action_detail")
    private String actionDetail;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
} 