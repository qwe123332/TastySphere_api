package com.example.tastysphere_api.service;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.tastysphere_api.repository.PostRepository;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecommendationService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public List<Post> getRecommendedPosts(User user) {
        // 1. 获取用户最近浏览的帖子类别
        Set<String> recentCategories = getUserRecentCategories(user.getId());
        
        // 2. 获取用户关注的用户的最新帖子
        List<Post> followingPosts = postRepository.findByUserIdInAndApprovedTrue(
            getUserFollowingIds(user.getId()), 
            PageRequest.of(0, 10)
        ).getContent();
        
        // 3. 获取热门帖子
        List<Post> trendingPosts = getTrendingPosts();
        
        // 4. 合并结果并去重
        Set<Post> recommendedPosts = new LinkedHashSet<>();
        recommendedPosts.addAll(followingPosts);
        recommendedPosts.addAll(trendingPosts);
        
        return new ArrayList<>(recommendedPosts);
    }

    private Set<String> getUserRecentCategories(Long userId) {
        String key = "user:categories:" + userId;
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    private List<Long> getUserFollowingIds(Long userId) {
        String key = "user:following:" + userId;
        return redisTemplate.opsForSet().members(key)
            .stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }

    private List<Post> getTrendingPosts() {
        String key = "trending:posts";
        Set<String> postIds = redisTemplate.opsForZSet().reverseRange(key, 0, 9);
        return postIds.stream()
            .map(id -> postRepository.findById(Long.parseLong(id)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    // 记录用户浏览行为
    public void recordUserView(Long userId, Post post) {
        try {
            String trendingKey = "trending:posts";
            redisTemplate.opsForZSet().incrementScore(trendingKey, post.getId().toString(), 1);
        } catch (Exception e) {
            log.error("Failed to record user view: {}", e.getMessage());
            // 可以选择吞掉异常，因为这不是核心功能
        }
    }
} 