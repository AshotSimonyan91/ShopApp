package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.productRequestDto.CurrentProductDto;
import am.shopappRest.shoppingApplicationRest.restDto.productRequestDto.ProductPaginationDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.FilterProductDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Value("${site.url.web}")
    private String siteURL;

    @GetMapping
    public ResponseEntity<ProductPaginationDto> getProductsWithPagination(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "9") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductDto> result = productService.findAllProducts(pageable);
        ProductPaginationDto productPaginationDto = new ProductPaginationDto();
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            productPaginationDto.setPageNumbers(pageNumbers);
        }
        productPaginationDto.setTotalPages(totalPages);
        productPaginationDto.setPage(page);
        productPaginationDto.setResult(result);
        return ResponseEntity.ok(productPaginationDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<CurrentProductDto> getCurrentProduct(@PathVariable("id") int id,@AuthenticationPrincipal CurrentUser currentUser) {
        CurrentProductDto currentProductDto = new CurrentProductDto();
        currentProductDto.setProductDto(productService.findById(id, currentUser.getUser()));
        return ResponseEntity.ok(currentProductDto);
    }

    @PostMapping("/add")
    public ResponseEntity<ProductDto> addProduct(@RequestBody CreateProductRequestDto createProductRequestDto, @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("files") MultipartFile[] files) throws IOException {
        return ResponseEntity.ok(productService.save(createProductRequestDto,files,currentUser.getUser()));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ProductDto>> getByFilter(@RequestParam(name = "size", defaultValue = "20") int size, @RequestParam(value = "page", defaultValue = "0") int page, @RequestBody FilterProductDto filterProductDto) {
        return ResponseEntity.ok(productService.search(page, size, filterProductDto));
    }


}
