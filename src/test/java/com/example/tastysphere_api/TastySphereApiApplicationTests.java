package com.example.tastysphere_api;

import com.example.tastysphere_api.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TastySphereApiApplicationTests {

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void contextLoads() {
    }
    @Test
    void testFindByName() {
        var permission = permissionRepository.findByName("USER_READ");
        assert permission.isPresent() : "USER_READ 权限未找到！";
    }
    @Test
    void deleteByName() {
        permissionRepository.deleteByName("USER_READ");
        var permission = permissionRepository.findByName("USER_READ");
        assert permission.isEmpty() : "USER_READ 权限未删除！";
    }



}
