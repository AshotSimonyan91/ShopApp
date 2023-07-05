package am.shoppingCommon.shoppingApplication.dto.commentDto;

import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
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
public class CommentDto {

    private int id;
    private String comment;
    private LocalDateTime commentDateTime;
    private UserDto user;
    private ProductDto product;
}
