package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Page<Product> findAllProducts(Pageable pageable);

    Page<Product> findByName(String name,Pageable pageable);

    List<Product> findAll();

    void remove(int id);


    void save(CreateProductRequestDto product, MultipartFile[] files, CurrentUser currentUser) throws IOException;

    Product findBy_Id(int productId);

    CreateProductResponseDto findById(int id);
}
