package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashot Simonyan on 08.06.23.
 */

public class CategoryMapper {

    public static Category dtoToCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setParentCategory(categoryDto.getParentCategory());
        category.setImage(categoryDto.getImage());
        return category;
    }

    public static CategoryDto categoryToDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setParentCategory(category.getParentCategory());
        categoryDto.setImage(category.getImage());
        return categoryDto;
    }

    public static List<CategoryDto> categoryDtoList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categories) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setImage(category.getImage());
            categoryDto.setParentCategory(category.getParentCategory());
            categoryDto.setName(category.getName());
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

}
