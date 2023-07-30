package am.shopappweb.shopappweb.controller;


import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * This controller handles category-related functionalities and serves the corresponding views.
 * It allows administrators to add a new category with an associated picture and remove existing categories.
 */
@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoryService categoryService;

    /**
     * Handles the request to add a new category with the provided CategoryDto and an associated picture.
     * The new category is saved using the CategoryService, and upon successful creation, redirects to the add category page for administrators.
     *
     * @param categoryDto   The CategoryDto containing information about the new category.
     * @param multipartFile The MultipartFile representing the picture associated with the new category.
     * @return The redirect URL to the add category page in the admin section.
     * @throws IOException if there are errors while processing the picture file.
     */

    @PostMapping("/add")
    public String addCategory(@ModelAttribute CategoryDto categoryDto, @RequestParam("pic") MultipartFile multipartFile) throws IOException {
        categoryService.save(categoryDto, multipartFile);
        return "redirect:/admin/add/category";
    }

    /**
     * Handles the request to remove an existing category with the specified ID.
     * The category is removed from the system using the CategoryService, and upon successful removal, redirects to the category page with the given categoryId.
     *
     * @param id          The ID of the category to be removed.
     * @param categoryId  The ID of the category page to redirect after successful removal.
     * @return The redirect URL to the category page with the specified categoryId.
     */
    @GetMapping("/remove")
    public String removeCategory(@RequestParam("id") int id, @RequestParam("categoryId") int categoryId) {
        categoryService.remove(id);
        return "redirect:/category/" + categoryId;
    }

}
