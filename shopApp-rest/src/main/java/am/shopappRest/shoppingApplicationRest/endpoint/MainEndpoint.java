package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.mainRequestDto.SearchPaginationDto;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.MainService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

/**
 * RestController class serving as the main endpoint for various API operations.
 * It provides endpoints to fetch parent categories with their child categories,
 * retrieve images, and perform product search with pagination.
 */
@RestController
@RequiredArgsConstructor
public class MainEndpoint {

    private final MainService mainService;
    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * Retrieves a map of parent categories along with their child categories.
     *
     * @return ResponseEntity with the map of parent categories and child categories as a JSON response.
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, List<CategoryDto>>> getParentCategoriesWithChildCategories() {
        return ResponseEntity.ok(categoryService.getParentCategoriesWithChildren());
    }


    /**
     * Retrieves the image data corresponding to the given image name.
     *
     * @param imageName The name of the image to retrieve.
     * @return ResponseEntity with the image data as a byte array and the appropriate content type.
     * @throws IOException if there is an error while reading the image data.
     */
    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@RequestParam("profilePic") String imageName) throws IOException {
        return ResponseEntity.ok(mainService.getImage(imageName));
    }


    /**
     * Performs a search for products based on the provided search value with pagination support.
     *
     * @param value The search value to be used for product search.
     * @param page  The page number for the search results (default value is 1 if not provided).
     * @param size  The number of products to be displayed per page (default value is 9 if not provided).
     * @return ResponseEntity with the search results, pagination information, and search value as a JSON response.
     */
    @GetMapping("/search")
    public ResponseEntity<SearchPaginationDto> searchPage(@RequestParam("value") String value,
                                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                          @RequestParam(value = "size", defaultValue = "9") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductDto> result = productService.findByName(value, pageable);
        SearchPaginationDto searchPaginationDto = new SearchPaginationDto();
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().toList();
            searchPaginationDto.setPageNumbers(pageNumbers);
        }
        searchPaginationDto.setTotalPages(totalPages);
        searchPaginationDto.setPage(page);
        searchPaginationDto.setResult(result);
        searchPaginationDto.setValue(value);
        return ResponseEntity.ok(searchPaginationDto);
    }
}
