package am.shoppingCommon.shoppingApplication.repository;


import am.shoppingCommon.shoppingApplication.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    void deleteByCart_IdAndProduct_Id(int cartId, int productId);
    List<CartItem> findTop4ByCartIdOrderByIdDesc(int cartId);

}
