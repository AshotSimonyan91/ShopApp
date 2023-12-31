package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.FilterProductDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductDto> findTrendingProducts();
    List<ProductDto> search(int page, int size, FilterProductDto filterProductDto);

    Page<ProductDto> findAllProducts(Pageable pageable);

    List<ProductDto> last3ByCategory(String category);
    Page<ProductDto> findByName(String name, Pageable pageable);

    List<ProductDto> findAll();

    void remove(int id, User user);

    ProductDto save(CreateProductRequestDto product, MultipartFile[] files, User user) throws IOException;

    ProductDto findById(int id, User user);

    Page<ProductDto> findByCategory(Pageable pageable,String Category);
}
