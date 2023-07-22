package am.shoppingCommon.shoppingApplication.repository;

import am.shoppingCommon.shoppingApplication.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    Optional<ProductReview> findByProductId(int productId);
    Optional<ProductReview> findByProductIdAndUserId(int productId, int userId);


}
