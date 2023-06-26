package am.shoppingCommon.shoppingApplication.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {
    private LocalDateTime dateTime;
    private double totalAmount;
    private List<OrderItemDto> orderItems;
}
