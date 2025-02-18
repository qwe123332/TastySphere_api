package com.example.tastysphere_api.repository;

import com.example.tastysphere_api.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
} 