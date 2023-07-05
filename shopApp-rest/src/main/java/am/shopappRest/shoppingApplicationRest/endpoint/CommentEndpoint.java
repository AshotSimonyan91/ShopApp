package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
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
    public ResponseEntity<CommentDto> addComment(@RequestParam("id") int id,
                                                 @ModelAttribute CommentRequestDto commentRequestDto,
                                                 @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(commentService.save(commentRequestDto, currentUser.getUser(), id));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeComment(@RequestParam("comment_id") int id) {
        commentService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
