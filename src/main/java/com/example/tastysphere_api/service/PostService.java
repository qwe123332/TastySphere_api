package com.example.tastysphere_api.service;
import com.example.tastysphere_api.dto.mapper.PostMapper;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.repository.PostRepository;
import com.example.tastysphere_api.dto.PostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    // ✅ 直接从数据库获取 Post，并返回 DTO
    public PostDTO getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // 检查帖子是否已审核通过
        if (!post.isApproved() && post.isAudited()) {
            throw new ResourceNotFoundException("Post is not available");
        }

        return convertToDTO(post);
    }

    //getPosts
  // 获取帖子列表
    public Page<PostDTO> getPosts(User user, Pageable pageable) {
        long id = user.getId();
        // 如果用户是管理员，返回所有已审核的帖子
      Page<Post> P= postRepository.findAllAudited(pageable);

        if (user!=null&&user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {

            return postRepository.findAllAudited(pageable)
                    .map(this::convertToDTO);
        }
        // 否则，只返回已审核且已批准的帖子
        return postRepository.findAllAuditedAndApproved(pageable)
                .map(this::convertToDTO);
    }

    // ✅ 创建 Post
    public PostDTO createPost(PostDTO postDTO, User user) {
        Post post = new Post();
        post.setUser(user);
        // 进行敏感词过滤
        post.setContent(sensitiveWordService.filterContent(postDTO.getContent()));
        post.setVisibility(postDTO.getVisibility());
        post.setImages(postDTO.getImages());
        
        // 设置审核状态
        post.setAudited(false);
        post.setApproved(false);

        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }

    // ✅ 获取某个用户的所有帖子
    public List<PostDTO> getUserPosts(Long userId) {
        Optional<Post> posts = postRepository.findById(userId);
        return posts.stream().map(this::convertToDTO).toList();  // 转换为 DTO
    }

    // ✅ 删除帖子
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 检查权限（只有帖子作者或管理员可以删除）
        if (!post.getUser().getId().equals(currentUser.getId()) &&
                currentUser.getRoles().stream().noneMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        postRepository.deleteById(postId);
    }
    @Autowired
    private PostMapper postMapper;
    // ✅ DTO 转换方法（避免使用 PostMapper）
    private PostDTO convertToDTO(Post post) {
        return postMapper.toDTO(post);
    }

    public Page<PostDTO> getPostsByUser(User user, Pageable pageable) {
        return postRepository.findByUser(user, pageable)
                .map(this::convertToDTO);
    }

    public Page<PostDTO> getPostsByVisibility(Post.Visibility visibility, Pageable pageable) {
        return postRepository.findByVisibility(visibility, pageable)
                .map(this::convertToDTO);
    }

    public PostDTO updatePost(Long postId, PostDTO postDTO, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 检查权限
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this post");
        }

        // 更新内容并重置审核状态
        post.setContent(sensitiveWordService.filterContent(postDTO.getContent()));
        post.setVisibility(postDTO.getVisibility());
        post.setImages(postDTO.getImages());
        post.setAudited(false);
        post.setApproved(false);

        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }

    // 添加搜索方法
    public Page<PostDTO> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findByContentContaining(keyword, pageable)
                .map(this::convertToDTO);
    }

    // 获取推荐帖子
    public List<PostDTO> getRecommendedPosts(User user) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdTime").descending());
        return postRepository.findByAuditedTrueAndApprovedTrue(pageable)
                .getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    

    // 点赞帖子
    public void likePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        // 这里应该检查用户是否已经点赞，避免重复点赞
        // 实际实现可能需要一个额外的点赞关系表
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    // 取消点赞
    public void unlikePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        // 检查是否已点赞
        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
        }
    }
}
