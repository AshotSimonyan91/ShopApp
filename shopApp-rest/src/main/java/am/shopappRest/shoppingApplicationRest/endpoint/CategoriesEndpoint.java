package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesEndpoint {
    private final CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@ModelAttribute CategoryDto categoryDto, @RequestParam("pic") MultipartFile multipartFile) throws IOException {
        categoryService.save(categoryDto, multipartFile);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCategory(@RequestParam("id") int id, @RequestParam("categoryId") int categoryId) {
        categoryService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
