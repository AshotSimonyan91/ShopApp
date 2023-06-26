package am.shoppingCommon.shoppingApplication.dto.wishlistDto;

import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequestDto {
    private Set<CreateProductResponseDto> product;
}
