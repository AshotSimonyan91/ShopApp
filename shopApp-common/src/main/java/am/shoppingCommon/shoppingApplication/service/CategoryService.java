package am.shoppingCommon.shoppingApplication.service;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Category;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<Category> findAllCategory();

    void remove(int id);

    Category save(CategoryDto categoryDto, MultipartFile multipartFile) throws IOException;
    Category save(CategoryDto categoryDto);
    Category save(int id, MultipartFile multipartFile) throws IOException;

    Category findById(Integer id);

    Map<String, List<CategoryDto>> getParentCategoriesWithChildren();
}
