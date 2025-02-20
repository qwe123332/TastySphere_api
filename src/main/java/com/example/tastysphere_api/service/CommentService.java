package com.example.tastysphere_api.service;

import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.dto.mapper.CommentMapper;
import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
}