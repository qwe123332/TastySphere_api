package com.example.tastysphere_api.service;

import com.example.tastysphere_api.dto.NotificationDTO;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.dto.mapper.UserMapper;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    private final Map<Long, UserDTO> userCache = new ConcurrentHashMap<>();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUser(User user) {
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().matches("^\\d{11}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 获取用户资料
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToDTO(user);
    }

    // 更新用户资料
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAvatar(userDTO.getAvatar());
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // 获取用户的帖子
    public Page<PostDTO> getUserPosts(Long userId, PageRequest pageRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return postService.getPostsByUser(user, pageRequest);
    }

    // 关注用户
    public void followUser(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }
        
        // 实际实现需要一个关注关系表
        // 这里只是示例实现
    }

    // 取消关注
    public void unfollowUser(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("User cannot unfollow themselves");
        }
        
        // 实际实现需要一个关注关系表
        // 这里只是示例实现
    }

    // 获取通知
    public Page<NotificationDTO> getNotifications(Long userId, PageRequest pageRequest) {
        // 实际实现需要一个通知表
        // 返回空页面作为示例
        return Page.empty(pageRequest);
    }

    // 标记通知为已读
    public void markNotificationAsRead(Long notificationId, Long userId) {
        // 实际实现需要一个通知表
        // 这里只是示例实现
    }
    @Autowired
    private UserMapper userMapper;
    // DTO 转换方法
    private UserDTO convertToDTO(User user) {
        System.out.println("userMapper.toDTO(user)"+userMapper.toDTO(user).getUsername());
        UserDTO userDTO = userMapper.toDTO(user);
        return  userMapper.toDTO(user);

    }
}