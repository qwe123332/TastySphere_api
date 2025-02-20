package com.example.tastysphere_api.dto.mapper;

import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")

public interface CommentMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "replies", source = "replies")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userAvatar", source = "user.avatar")
    @Mapping(target = "likeCount", source = "likeCount")
    @Mapping(target = "createdAt", source = "createdTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "content", source = "content")
    CommentDTO toDTO(Comment comment);

    List<CommentDTO> toDTOList(List<Comment> comments);

}
