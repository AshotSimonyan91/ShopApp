package am.shoppingCommon.shoppingApplication.dto.commentDto;

import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDto {

    private String comment;
    private Date commentDateTime;
    private ProductDto productDto;
    private UserDto userDto;
    private CreateProductRequestDto productRequestDto;
}
