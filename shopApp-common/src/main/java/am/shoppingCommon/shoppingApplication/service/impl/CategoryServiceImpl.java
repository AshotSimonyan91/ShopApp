package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Category;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shoppingCommon.shoppingApplication.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ashot Simonyan on 21.05.23.
 * This class provides the implementation for the CategoryService interface, offering functionalities
 * related to managing categories in a shopping application. It handles operations such as retrieving all categories,
 * finding categories by parent, retrieving category by ID, saving new categories with image uploads,
 * and removing categories. The class uses repositories to interact with the underlying data storage
 * and a custom CategoryMapper to convert entities to DTOs and vice versa.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    /**
     * Retrieves all categories available in the shopping application.
     *
     * @return The List of CategoryDto representing all categories.
     */
    @Override
    public List<CategoryDto> findAllCategory() {
        List<Category> all = categoryRepository.findAll();
        log.info("Get all categories");
        return CategoryMapper.categoryDtoList(all);
    }

    /**
     * Removes the category with the specified ID from the database.
     *
     * @param id The unique identifier of the category to be removed.
     */
    @Override
    public void remove(int id) {
        categoryRepository.deleteById(id);
        log.info("category was deleted");
    }

    /**
     * Saves a new category with the provided CategoryDto and associated image (if available) to the database.
     * The image is uploaded to the specified imageUploadPath. If the image is not provided, the category is saved without it.
     *
     * @param categoryDto  The CategoryDto representing the new category information.
     * @param multipartFile The image file associated with the new category (optional).
     * @return The CategoryDto representing the saved category with the assigned image.
     * @throws IOException If there's an issue with uploading or processing the image file.
     */
    @Override
    public CategoryDto save(CategoryDto categoryDto, MultipartFile multipartFile) throws IOException {
        Category category = CategoryMapper.dtoToCategory(categoryDto);
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
            category.setImage(fileName);
        }
        Category save = categoryRepository.save(category);
        log.info("Category saved in {} id",save.getId());
        return CategoryMapper.categoryToDto(save);
    }

    /**
     * Retrieves all categories having the specified parent category.
     *
     * @param parent The name of the parent category.
     * @return The List of CategoryDto representing categories having the specified parent category.
     */
    public List<CategoryDto> findByParent(String parent) {
        List<Category> allByParentCategory = categoryRepository.findAllByParentCategory(parent);
        log.info("Get all category by parent category");
        return CategoryMapper.categoryDtoList(allByParentCategory);
    }

    /**
     * Retrieves the category with the specified ID from the database.
     *
     * @param id The unique identifier of the category to be retrieved.
     * @return The CategoryDto representing the retrieved category, or null if no category is found.
     */
    @Override
    public CategoryDto findById(Integer id) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isPresent()) {
            Category category = byId.get();
            log.info("Category found by {} id",id);
            return CategoryMapper.categoryToDto(category);
        }
        log.info("Category does not find");
        return null;
    }

    /**
     * Retrieves all categories from the database and organizes them into a map where the keys are the parent category names,
     * and the values are lists of CategoryDto representing the children categories. If a category has no parent category,
     * it is considered a top-level category. The method returns a map containing parent categories as keys and their children
     * categories as values.
     *
     * @return The Map of parent category names with their associated children categories.
     */
    public Map<String, List<CategoryDto>> getParentCategoriesWithChildren() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categories) {
            categoryDtoList.add(CategoryMapper.categoryToDto(category));
        }

        Map<String, List<CategoryDto>> parentCategoriesMap = new HashMap<>();

        for (CategoryDto categoryDto : categoryDtoList) {
            String parentCategory = categoryDto.getParentCategory();
            if (!parentCategoriesMap.containsKey(parentCategory)) {
                parentCategoriesMap.put(parentCategory, new ArrayList<>());
            }
            parentCategoriesMap.get(parentCategory).add(categoryDto);
        }
        log.info("Parent category found");
        return parentCategoriesMap;
    }

}
