package am.shoppingCommon.shoppingApplication.dto.orderDto;

import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderItemDto {
    private int id;
    private int count;
    private CreateProductResponseDto product;
    private OrderDto order;
}
