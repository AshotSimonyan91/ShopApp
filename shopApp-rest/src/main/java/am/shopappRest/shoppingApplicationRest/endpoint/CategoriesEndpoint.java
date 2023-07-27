package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesEndpoint {
    private final CategoryService categoryService;

    @Value("${site.url.web}")
    private String siteURL;

    @PostMapping("/add")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto, @RequestParam("pic") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(categoryService.save(categoryDto,multipartFile));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCategory(@RequestParam("id") int id) {
        categoryService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
