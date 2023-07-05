package am.shoppingCommon.shoppingApplication.dto.commentDto;

import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDto {
    private String comment;
    private UserDto userDto;
    private LocalDateTime commentDateTime;
}
