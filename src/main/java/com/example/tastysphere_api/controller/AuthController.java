package com.example.tastysphere_api.controller;
import com.example.tastysphere_api.dto.request.UserLoginRequest;
import com.example.tastysphere_api.dto.request.UserRegisterRequest;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.security.JwtTokenProvider;
import com.example.tastysphere_api.service.UserService;
import org.hibernate.engine.spi.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginRequest loginRequest) {
        try {
            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            // 认证成功后生成 JWT Token
            String token = tokenProvider.generateToken(authentication.getName());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest registerRequest) {
        // 处理注册逻辑
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已被注册");
        }
      User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setStatus(registerRequest.getStatus());
        user.setStatus(registerRequest.getStatus());
        // 头像字段这里不做设置，可留空

        // 设置创建和更新时间（这里简单设置为当前时间）
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedTime(now);
        user.setUpdatedTime(now);
        // 默认激活状态
        user.setActive(true);
        userService.createUser(user);
        return ResponseEntity.ok("注册成功");
    }
}
