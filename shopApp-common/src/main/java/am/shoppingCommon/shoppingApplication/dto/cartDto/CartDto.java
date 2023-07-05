package am.shoppingCommon.shoppingApplication.dto.cartDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private int id;
    private int userId;
    private List<CartItemDto> cartItems;
}
