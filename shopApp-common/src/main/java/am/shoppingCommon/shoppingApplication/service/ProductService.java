package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
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


    void save(CreateProductRequestDto product, MultipartFile[] files, User user) throws IOException;

    Product findBy_Id(int productId);

    Product findById(int id);
}
