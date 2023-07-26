package am.shoppingCommon.shoppingApplication.repository;


import am.shoppingCommon.shoppingApplication.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, QuerydslPredicateExecutor<Product> {
    List<Product> findByNameContainingIgnoreCase(String name);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Product> findTop9ByOrderByReviewDesc();

    Optional<Product> findAllByUser_Id(int userId);

    List<Product> findProductsByName(String name);
    Page<Product> findAllByCategoriesName(String categoryName, Pageable pageable);

}
