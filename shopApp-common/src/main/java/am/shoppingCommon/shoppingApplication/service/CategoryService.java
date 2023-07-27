package am.shoppingCommon.shoppingApplication.service;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<CategoryDto> findAllCategory();

    List<CategoryDto> findByParent(String parent);

    void remove(int id);

    CategoryDto save(CategoryDto categoryDto, MultipartFile multipartFile) throws IOException;

    CategoryDto findById(Integer id);

    Map<String, List<CategoryDto>> getParentCategoriesWithChildren();
}
