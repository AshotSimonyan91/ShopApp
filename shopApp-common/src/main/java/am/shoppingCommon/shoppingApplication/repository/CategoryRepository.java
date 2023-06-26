package am.shoppingCommon.shoppingApplication.repository;


import am.shoppingCommon.shoppingApplication.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByParentCategory(String name);
}
