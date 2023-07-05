package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Page<ProductDto> findAllProducts(Pageable pageable);

    Page<ProductDto> findByName(String name, Pageable pageable);

    List<ProductDto> findAll();

    void remove(int id);


    ProductDto save(CreateProductRequestDto product, MultipartFile[] files, User user) throws IOException;
    ProductDto save(CreateProductRequestDto product, User user);
    ProductDto save(int id, MultipartFile[] files) throws IOException;

    ProductDto findById(int id);
}
