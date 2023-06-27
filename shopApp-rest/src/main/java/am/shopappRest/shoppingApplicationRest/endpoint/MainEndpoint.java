package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.MainService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import am.shopappRest.shoppingApplicationRest.restDto.mainRequestDto.SearchPaginationDto;
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

@RestController
@RequiredArgsConstructor
public class MainEndpoint {

    private final MainService mainService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<Map<String, List<CategoryDto>>> main() {
        return ResponseEntity.ok(categoryService.getParentCategoriesWithChildren());
    }

    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@RequestParam("profilePic") String imageName) throws IOException {
        return ResponseEntity.ok(mainService.getImage(imageName));
    }

    @GetMapping("/search")
    public ResponseEntity<SearchPaginationDto> searchPage(@RequestParam("value") String value,
                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", defaultValue = "9") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> result = productService.findByName(value, pageable);
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
