package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Comment;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.CommentMapper;
import am.shoppingCommon.shoppingApplication.repository.CommentsRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 * This class provides the implementation for the CommentService interface, offering functionalities
 * related to managing comments for products in a shopping application. It handles operations such as
 * adding new comments, retrieving comments for a specific product, and removing comments. The class uses
 * repositories to interact with the underlying data storage and a custom CommentMapper to convert entities
 * to DTOs and vice versa.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Removes the comment with the specified ID from the database.
     *
     * @param id The unique identifier of the comment to be removed.
     */
    @Override
    public void remove(int id) {
        commentsRepository.deleteById(id);
        log.info("Comment was deleted");
    }

    /**
     * Saves a new comment for the specified product and user. The comment details are provided in the
     * CommentRequestDto, and the comment is associated with the product and user in the database.
     *
     * @param commentRequestDto The CommentRequestDto containing the comment details.
     * @param user              The user who posted the comment.
     * @param productId         The unique identifier of the product to which the comment is associated.
     * @return The CommentDto representing the saved comment, or null if the comment is empty or the associated product
     *         or user cannot be found.
     */
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
                log.info("Comment was saved in {} id by {} user_id",save.getId(),user.getId());
                return CommentMapper.toDto(save);
            }
        }
        log.info("Comment did not save");
        return null;
    }

    /**
     * Retrieves all comments associated with the specified product from the database.
     *
     * @param productId The unique identifier of the product whose comments are to be retrieved.
     * @return The List of Comment containing all comments associated with the product.
     */
    @Override
    public List<Comment> findAllByLimit(int productId) {
        List<Comment> allByProductId = commentsRepository.findAllByProduct_Id(productId);
        log.info("Get all comments by {} product id",productId);
        return allByProductId;
    }
}
