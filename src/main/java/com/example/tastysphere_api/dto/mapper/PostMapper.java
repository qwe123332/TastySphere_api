package com.example.tastysphere_api.dto.mapper;

import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.Comment;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
    @Mapping(target = "isLiked", ignore = true)  // Set in service layer
    @Mapping(target = "isMine", ignore = true)   // Set in service layer
    @Mapping(target = "commentDTOs", source = "comments", qualifiedByName = "mapComments") // Ensure comments are mapped correctly
    PostDTO toDTO(Post post);

    // Convert list of posts to DTOs
    List<PostDTO> toDTOList(List<Post> posts);

    // Convert comments to DTOs safely
    @Named("mapComments")
    default List<CommentDTO> mapComments(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return commentsToCommentDTOs(comments);
    }

    List<CommentDTO> commentsToCommentDTOs(List<Comment> comments);

    // Enhanced method to handle `isLiked` and `isMine`
    @Named("toDTOWithUserContext")
    default PostDTO toDTOWithUserContext(Post post, Long currentUserId, Set<Long> likedPostIds) {
        PostDTO dto = toDTO(post);
        dto.setIsMine(post.getUser() != null && post.getUser().getId().equals(currentUserId));
        dto.setIsLiked(likedPostIds != null && likedPostIds.contains(post.getId()));
        return dto;
    }

    // Utility method to prevent null values
    @AfterMapping
    default void afterMapping(@MappingTarget PostDTO target) {
        if (target.getImages() == null) {
            target.setImages(new ArrayList<>());
        }
        if (target.getLikeCount() == null) {
            target.setLikeCount(0);
        }
        if (target.getCommentCount() == null) {
            target.setCommentCount(0);
        }
    }
}
