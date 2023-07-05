package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Comment;
import am.shoppingCommon.shoppingApplication.entity.User;

import java.util.List;

public class CommentMapper {
    public static Comment map(CommentRequestDto commentRequestDto, User user) {
        if (commentRequestDto == null) {
            return null;
        }
        return Comment.builder()
                .comment(commentRequestDto.getComment())
                .user(user)
                .build();
    }

    public static List<CommentResponseDto> map(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(CommentMapper::mapToResponseDto)
                .toList();
    }

    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .commentDateTime(comment.getDateTime())
                .user(UserMapper.userToUserDto(comment.getUser()))
                .product(ProductMapper.mapToDto(comment.getProduct()))
                .build();
    }

    public static CommentResponseDto mapToResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentResponseDto.builder()
                .comment(comment.getComment())
                .commentDateTime(comment.getDateTime())
                .userDto(UserMapper.userToUserDto(comment.getUser()))
                .build();
    }

    public static List<CommentDto> mapToListCommentDto(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }
}
