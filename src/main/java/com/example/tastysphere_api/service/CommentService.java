package com.example.tastysphere_api.service;

import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.dto.mapper.CommentMapper;
import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    public List<CommentDTO> getCommentsByPost(Long postId) {
        List<Comment> topLevelComments = commentRepository.findByPostIdAndParentCommentIsNull(postId);
        return topLevelComments.stream()
                .map(comment -> {
                    CommentDTO dto = commentMapper.toDTO(comment);
                    List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());
                    dto.setReplies(commentMapper.toDTOList(replies));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Page<CommentDTO> getPostComments(Long postId, Pageable pageable) {
        // 1. 查询顶层评论（分页）
        Page<Comment> commentsPage = commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable);

        // 2. 批量预加载所有顶层评论的回复（避免 N+1）
        List<Comment> topLevelComments = commentsPage.getContent();
        List<Long> parentIds = topLevelComments.stream().map(Comment::getId).collect(Collectors.toList());
        Map<Long, List<Comment>> repliesMap = commentRepository.findByParentCommentIdIn(parentIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getParentComment().getId()));

        // 3. 转换为 DTO 并填充 replies
        return commentsPage.map(comment -> {
            CommentDTO dto = commentMapper.toDTO(comment);
            dto.setReplies(commentMapper.toDTOList(repliesMap.getOrDefault(comment.getId(), Collections.emptyList())));
            return dto;
        });
    }

    public Page<Comment> getCommentReplies(Long parentCommentId, Pageable pageable) {
        return commentRepository.findByParentCommentId(parentCommentId, pageable);
    }
}