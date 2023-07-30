package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * This endpoint class provides REST API endpoints for handling category-related operations in the shopping application.
 * It allows administrators to add new categories and remove existing categories. The class uses Spring's @RestController
 * annotation to handle incoming requests and returns ResponseEntity objects with appropriate response data.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesEndpoint {
    private final CategoryService categoryService;

    @Value("${site.url.web}")
    private String siteURL;

    /**
     * POST endpoint that handles adding a new category to the application. It takes a CategoryDto object containing the
     * details of the new category and a MultipartFile object representing the category image. The method returns a
     * ResponseEntity containing the newly created CategoryDto object with category information, including the image URL.
     *
     * @param categoryDto  The CategoryDto object containing the details of the new category.
     * @param multipartFile The MultipartFile object representing the category image.
     * @return ResponseEntity containing the newly created CategoryDto object with category information.
     * @throws IOException If there is an error with the file upload or processing.
     */
    @PostMapping("/add")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto, @RequestParam("pic") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(categoryService.save(categoryDto,multipartFile));
    }

    /**
     * DELETE endpoint that handles removing an existing category from the application. It takes the ID of the category
     * to be removed as a request parameter. The method returns a ResponseEntity with no content to indicate successful removal.
     *
     * @param id The ID of the category to be removed.
     * @return ResponseEntity with no content to indicate successful removal.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCategory(@RequestParam("id") int id) {
        categoryService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
