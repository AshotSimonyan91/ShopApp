package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Category;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shoppingCommon.shoppingApplication.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    @Override
    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public void remove(int id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category save(CategoryDto categoryDto, MultipartFile multipartFile) throws IOException {
        Category category = CategoryMapper.dtoToCategory(categoryDto);
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
            category.setImage(fileName);
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category save(CategoryDto categoryDto) {
        Category category = CategoryMapper.dtoToCategory(categoryDto);
        return categoryRepository.save(category);
    }

    @Override
    public Category save(int id, MultipartFile multipartFile) throws IOException {
        Category category = categoryRepository.findById(id).orElse(null);
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
            category.setImage(fileName);
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category findById(Integer id) {
        Optional<Category> byId = categoryRepository.findById(id);
        return byId.get();
    }

    public Map<String, List<CategoryDto>> getParentCategoriesWithChildren() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories) {
            categoryDtos.add(CategoryMapper.categoryToDto(category));
        }

        Map<String, List<CategoryDto>> parentCategoriesMap = new HashMap<>();

        for (CategoryDto categoryDto : categoryDtos) {
            String parentCategory = categoryDto.getParentCategory();
            if (!parentCategoriesMap.containsKey(parentCategory)) {
                parentCategoriesMap.put(parentCategory, new ArrayList<>());
            }
            parentCategoriesMap.get(parentCategory).add(categoryDto);
        }

        return parentCategoriesMap;
    }
}
