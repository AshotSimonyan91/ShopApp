package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Comment;
import am.shoppingCommon.shoppingApplication.entity.User;

import java.util.List;

public interface CommentService {

    void remove(int id);

    CommentDto save(CommentRequestDto commentRequestDto, User user, int productId);

    List<Comment> findAllByLimit(int productId);
}
