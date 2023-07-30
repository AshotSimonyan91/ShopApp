package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * This endpoint class provides REST API endpoints for handling comments related to products in the shopping application.
 * It allows users to add new comments for products and remove their own comments. The class uses Spring's @RestController
 * annotation to handle incoming requests and returns ResponseEntity objects with appropriate response data.
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentEndpoint {
    private final CommentService commentService;

    /**
     * POST endpoint that handles adding a new comment for a product in the application. It takes the ID of the product to which
     * the comment belongs, the CommentRequestDto object containing the details of the comment, and the CurrentUser object
     * representing the currently authenticated user. The method returns a ResponseEntity containing the newly created CommentDto
     * object with comment information, including the user who posted it and the product it belongs to.
     *
     * @param id                 The ID of the product to which the comment belongs.
     * @param commentRequestDto  The CommentRequestDto object containing the details of the new comment.
     * @param currentUser        The CurrentUser object representing the currently authenticated user.
     * @return ResponseEntity containing the newly created CommentDto object with comment information.
     */
    @PostMapping("/add")
    public ResponseEntity<CommentDto> addComment(@RequestParam("id") int id,
                                                 @ModelAttribute CommentRequestDto commentRequestDto,
                                                 @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(commentService.save(commentRequestDto, currentUser.getUser(), id));
    }

    /**
     * DELETE endpoint that handles removing an existing comment from the application. It takes the ID of the comment to be removed
     * as a request parameter. The method checks if the user who initiated the request owns the comment, and if so, it removes the
     * comment from the system. The method returns a ResponseEntity with no content to indicate successful removal.
     *
     * @param id The ID of the comment to be removed.
     * @return ResponseEntity with no content to indicate successful removal.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeComment(@RequestParam("comment_id") int id) {
        commentService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
