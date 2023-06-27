package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.productRequestDto.CurrentProductDto;
import am.shopappRest.shoppingApplicationRest.restDto.productRequestDto.ProductPaginationDto;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shoppingCommon.shoppingApplication.mapper.CommentMapper;
import am.shoppingCommon.shoppingApplication.mapper.ProductMapper;
import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ProductPaginationDto> productPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "9") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> result = productService.findAllProducts(pageable);
        ProductPaginationDto productPaginationDto = new ProductPaginationDto();
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            productPaginationDto.setPageNumbers(pageNumbers);
        }
        productPaginationDto.setTotalPages(totalPages);
        productPaginationDto.setPage(page);
        productPaginationDto.setResult(result);
        return ResponseEntity.ok(productPaginationDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<CurrentProductDto> currentProductPage(@PathVariable("id") int id) {
        CurrentProductDto currentProductDto = new CurrentProductDto();
        currentProductDto.setCreateProductResponseDto(ProductMapper.mapToResponseDto(productService.findById(id)));
        currentProductDto.setProductDtos(ProductMapper.mapToListDto(productService.findAll()));
        currentProductDto.setCommentDtos(CommentMapper.map(commentService.findAllByLimit(id)));
        return ResponseEntity.ok(currentProductDto);
    }

    @GetMapping("/add")
    public ResponseEntity<List<CategoryDto>> addProductPage(ModelMap modelMap) {
        List<CategoryDto> allCategory = CategoryMapper.categoryDtoList(categoryService.findAllCategory());
        return ResponseEntity.ok(allCategory);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody CreateProductRequestDto createProductRequestDto,
                                        @AuthenticationPrincipal CurrentUser currentUser,
                                        @RequestParam("files") MultipartFile[] files) throws IOException {
        productService.save(createProductRequestDto, files, currentUser);
        return ResponseEntity.ok().build();
    }

}
