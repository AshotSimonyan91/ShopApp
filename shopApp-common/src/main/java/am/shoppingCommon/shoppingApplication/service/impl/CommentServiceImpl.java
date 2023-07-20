package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.CommentService;
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
    private final UserRepository userRepository;

    @Override
    public void remove(int id) {
        commentsRepository.deleteById(id);
    }

    public CommentDto save(CommentRequestDto commentRequestDto, User user, int productId) {
        if (commentRequestDto.getComment() != null && !commentRequestDto.getComment().equals("")) {
            Optional<Product> byId = productRepository.findById(productId);
            if (byId.isPresent()) {
                Product product = byId.get();
                Optional<User> userOptional = userRepository.findById(user.getId());
                Comment comment = CommentMapper.map(commentRequestDto, userOptional.orElse(null));
                comment.setProduct(product);
                commentsRepository.save(comment);
                Comment save = commentsRepository.save(comment);
                return CommentMapper.toDto(save);
            }
        }
        return null;
    }

    @Override
    public List<Comment> findAllByLimit(int productId) {
        List<Comment> allByProductId = commentsRepository.findAllByProduct_Id(productId);
        return allByProductId;
    }
}
