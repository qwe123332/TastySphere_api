package com.example.tastysphere_api.service;


import com.example.tastysphere_api.entity.Permission;
import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.repository.PermissionRepository;
import com.example.tastysphere_api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 为指定角色分配权限
     *
     * @param roleName        角色名称 (ADMIN, USER, MERCHANT)
     * @param permissionNames 权限名称数组
     * @return true: 成功, false: 失败
     */
    public boolean assignPermissionsToRole(String roleName, String[] permissionNames) {
        // 查询角色
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            System.err.println("❌ 角色 " + roleName + " 不存在！");
            return false;
        }
        Role role = roleOptional.get();

        // 🔹 批量查询权限


        // 查询所有权限，避免循环查询数据库
        List<Permission> permissions = permissionRepository.findByNameIn(List.of(permissionNames));
        if (permissions.isEmpty()) {
            System.err.println("⚠️ 未找到任何匹配的权限！");
            return false;
        }

        // 更新角色权限
        Set<Permission> currentPermissions = role.getPermissions();
        currentPermissions.addAll(permissions);
        role.setPermissions(currentPermissions);

        // 保存角色
        roleRepository.save(role);
        System.out.println("✅ 已成功为角色 " + roleName + " 分配权限：" + permissions.stream().map(Permission::getName).collect(Collectors.joining(", ")));
        return true;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public boolean createRole(String name, String description) {
        try {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        roleRepository.save(role);
        return true;
        } catch (Exception e) {
            System.err.println("❌ 创建角色时发生错误: " + e.getMessage());
            return false;
        }
    }
}