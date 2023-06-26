package am.shoppingCommon.shoppingApplication.dto.orderDto;

import am.shoppingCommon.shoppingApplication.entity.OrderItem;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.entity.User;
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
public class OrderRequestDto {
    private int id;
    private LocalDateTime dateTime;
    private double totalAmount;
    private Status status;
    private User user;
    private List<OrderItem> orderItems;
}
