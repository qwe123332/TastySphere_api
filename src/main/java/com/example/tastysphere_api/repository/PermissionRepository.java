package com.example.tastysphere_api.repository;

import com.example.tastysphere_api.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);

    List<Permission> findByNameIn(List<String> names); // 🔹 批量查询权限

    @Transactional
    void deleteByName(String name);




}