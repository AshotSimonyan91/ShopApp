package am.shoppingCommon.shoppingApplication.repository;

import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Integer> {

    Optional<WishList> findByUserId(Integer id);

    Optional<WishList> findByProduct(Product product);

    void deleteAllByProduct_Id(Integer id);
}
