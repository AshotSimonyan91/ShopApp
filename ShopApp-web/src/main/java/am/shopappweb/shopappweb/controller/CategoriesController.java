package am.shopappweb.shopappweb.controller;


import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoryService categoryService;

    @PostMapping("/add")
    public String addCategory(@ModelAttribute CategoryDto categoryDto, @RequestParam("pic") MultipartFile multipartFile) throws IOException {
        categoryService.save(categoryDto, multipartFile);
        return "redirect:/admin/add/category";
    }

    @GetMapping("/remove")
    public String removeCategory(@RequestParam("id") int id, @RequestParam("categoryId") int categoryId) {
        categoryService.remove(id);
        return "redirect:/category/" + categoryId;
    }

}
