package com.example.tastysphere_api.mapper;

import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDTO toDTO(Comment comment);

    Comment toEntity(CommentDTO commentDTO);
} 