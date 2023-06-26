package am.shopappweb.shopappweb.service.impl;


import am.shopappweb.shopappweb.service.CommentService;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Comment;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.CommentMapper;
import am.shoppingCommon.shoppingApplication.repository.CommentsRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;
    private final ProductRepository productRepository;


    @Override
    public List<CommentResponseDto> findAllCategory() {
        return CommentMapper.map(commentsRepository.findAll());
    }

    @Override
    public void remove(int id) {
        commentsRepository.deleteById(id);
    }

    @Override
    public void save(CommentRequestDto commentRequestDto, User user, int productId) {
        if (commentRequestDto.getComment() != null && !commentRequestDto.getComment().equals("")) {
            Optional<Product> byId = productRepository.findById(productId);
            if (byId.isPresent()) {
                Product product = byId.get();
                Comment comment = CommentMapper.map(commentRequestDto, user);
                comment.setProduct(product);
                commentsRepository.save(comment);
            }
        }
    }

    @Override
    public List<CommentResponseDto> findAllByProductId(int id) {
        return CommentMapper.map(commentsRepository.findAllByProduct_Id(id));
    }

    @Override
    public List<Comment> findAllByLimit(int productId) {
        return commentsRepository.findAllByProduct_Id(productId);
    }
}
