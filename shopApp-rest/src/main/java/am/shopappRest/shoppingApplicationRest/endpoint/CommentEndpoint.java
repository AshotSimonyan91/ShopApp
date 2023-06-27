package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentEndpoint {
    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestParam("id") int id,
                                        @ModelAttribute CommentRequestDto commentRequestDto,
                                        @AuthenticationPrincipal CurrentUser currentUser) {
        commentService.save(commentRequestDto, currentUser.getUser(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeComment(@RequestParam("comment_id") int id, @RequestParam("product_id") int productId) {
        commentService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
