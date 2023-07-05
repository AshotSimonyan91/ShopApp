package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentResponseDto;
import am.shoppingCommon.shoppingApplication.entity.User;

import java.util.List;

public interface CommentService {

    List<CommentResponseDto> findAllCategory();

    void remove(int id);

    CommentDto save(CommentRequestDto commentRequestDto, User user, int productId);

    List<CommentResponseDto> findAllByProductId(int id);

    List<CommentDto> findAllByLimit(int productId);
}
