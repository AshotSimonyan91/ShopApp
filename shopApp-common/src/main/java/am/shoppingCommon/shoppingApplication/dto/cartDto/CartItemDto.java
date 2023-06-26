package am.shoppingCommon.shoppingApplication.dto.cartDto;

import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private int id;
    private int count;
    private ProductDto product;
}
