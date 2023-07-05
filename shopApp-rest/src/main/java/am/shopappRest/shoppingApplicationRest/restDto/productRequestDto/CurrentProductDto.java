package am.shopappRest.shoppingApplicationRest.restDto.productRequestDto;

import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentResponseDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentProductDto {

    private ProductDto productDto;
    private List<CommentDto> commentDtos;
}
