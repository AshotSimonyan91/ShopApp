package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.productRequestDto.CurrentProductDto;
import am.shopappRest.shoppingApplicationRest.restDto.productRequestDto.ProductPaginationDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.FilterProductDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
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


/**
 * RestController class responsible for handling product-related API operations.
 * It provides endpoints to get products with pagination, retrieve a single product,
 * add a new product, and search for products based on filtering criteria.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;
    @Value("${site.url.web}")
    private String siteURL;

    /**
     * Retrieves products with pagination support.
     *
     * @param page The page number for the product results (default value is 1 if not provided).
     * @param size The number of products to be displayed per page (default value is 9 if not provided).
     * @return ResponseEntity with the ProductPaginationDto containing product results and pagination details as a JSON response.
     */
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


    /**
     * Retrieves a single product with the given ID.
     *
     * @param id          The ID of the product to retrieve.
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with the CurrentProductDto containing the product details as a JSON response.
     */
    @GetMapping("{id}")
    public ResponseEntity<CurrentProductDto> getCurrentProduct(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        CurrentProductDto currentProductDto = new CurrentProductDto();
        currentProductDto.setProductDto(productService.findById(id, currentUser.getUser()));
        return ResponseEntity.ok(currentProductDto);
    }


    /**
     * Adds a new product based on the provided CreateProductRequestDto and image files.
     *
     * @param createProductRequestDto The DTO containing details of the product to be added.
     * @param currentUser             The information of the currently authenticated user.
     * @param files                   The array of MultipartFile objects representing product images.
     * @return ResponseEntity with the created ProductDto as a JSON response.
     * @throws IOException if there is an error while processing the image files.
     */
    @PostMapping("/add")
    public ResponseEntity<ProductDto> addProduct(@RequestBody CreateProductRequestDto createProductRequestDto, @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("files") MultipartFile[] files) throws IOException {
        return ResponseEntity.ok(productService.save(createProductRequestDto, files, currentUser.getUser()));
    }


    /**
     * Searches for products based on the provided FilterProductDto with pagination support.
     *
     * @param size             The number of products to be displayed per page (default value is 20 if not provided).
     * @param page             The page number for the search results (default value is 0 if not provided).
     * @param filterProductDto The DTO containing the filter criteria for product search.
     * @return ResponseEntity with the list of ProductDto representing the search results as a JSON response.
     */
    @PostMapping("/search")
    public ResponseEntity<List<ProductDto>> getByFilter(@RequestParam(name = "size", defaultValue = "20") int size, @RequestParam(value = "page", defaultValue = "0") int page, @RequestBody FilterProductDto filterProductDto) {
        return ResponseEntity.ok(productService.search(page, size, filterProductDto));
    }


}
