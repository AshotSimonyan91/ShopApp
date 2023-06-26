package am.shoppingCommon.shoppingApplication.dto.wishlistDto;

import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDto {
    private int id;
    private User user;
    private Set<ProductDto> product;
}
