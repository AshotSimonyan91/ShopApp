package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Category;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
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
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(CategoryMapper.categoryToDto(categoryService.save(categoryDto)));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<?> addCategory(@PathVariable("id") int id,
                                         @RequestParam("pic") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(CategoryMapper.categoryToDto(categoryService.save(id, multipartFile)));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCategory(@RequestParam("id") int id) {
        categoryService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
