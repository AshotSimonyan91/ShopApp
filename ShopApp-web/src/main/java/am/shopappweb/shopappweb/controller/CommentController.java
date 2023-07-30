package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles comment-related functionalities and serves the corresponding views.
 * It allows users to add new comments to products and remove their own comments from products.
 */
@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /**
     * Handles the request to add a new comment to a product with the specified ID.
     * The comment is saved using the CommentService, associated with the currently authenticated user, and redirected back to the product page with the given productId.
     *
     * @param id                The ID of the product to which the comment is added.
     * @param commentRequestDto The CommentRequestDto containing information about the new comment.
     * @param currentUser       The currently authenticated user (CurrentUser) who adds the comment.
     * @return The redirect URL to the product page with the specified productId.
     */
    @PostMapping("/add")
    public String addComment(@RequestParam("id") int id,
                                              @ModelAttribute CommentRequestDto commentRequestDto,
                                              @AuthenticationPrincipal CurrentUser currentUser) {
        commentService.save(commentRequestDto,currentUser.getUser(),id);
        return "redirect:/products/" + id;
    }

    /**
     * Handles the request to remove an existing comment with the specified ID.
     * The comment is removed from the system using the CommentService, and upon successful removal, redirects back to the product page with the given productId.
     *
     * @param id        The ID of the comment to be removed.
     * @param productId The ID of the product page to redirect after successful removal.
     * @return The redirect URL to the product page with the specified productId.
     */
    @GetMapping("/remove")
    public String removeComment(@RequestParam("comment_id") int id, @RequestParam("product_id") int productId) {
        commentService.remove(id);
        return "redirect:/products/" + productId;
    }
}
