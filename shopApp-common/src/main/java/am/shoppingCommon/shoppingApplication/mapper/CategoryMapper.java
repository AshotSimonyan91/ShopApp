package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ashot Simonyan on 08.06.23.
 */
public class CategoryMapper {

    public static Category dtoToCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .parentCategory(categoryDto.getParentCategory())
                .image(categoryDto.getImage())
                .build();
    }

    public static CategoryDto categoryToDto(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategory(category.getParentCategory())
                .image(category.getImage())
                .build();
    }

    public static List<CategoryDto> categoryDtoList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(CategoryMapper::categoryToDto)
                .toList();
    }

    public static List<Category> dtoListMapper(List<CategoryDto> categories) {
        if (categories == null) {
            return Collections.emptyList();
        }

        List<Category> categoryList = new ArrayList<>();
        for (CategoryDto categoryDto : categories) {
            Category category = new Category();
            category.setId(categoryDto.getId());
            category.setParentCategory(categoryDto.getParentCategory());
            category.setName(categoryDto.getName());
            categoryList.add(category);
        }
        return categoryList;
    }


}
