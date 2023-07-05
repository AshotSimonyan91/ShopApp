package am.shoppingCommon.shoppingApplication.service.impl;

import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.FilterProductDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.Image;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.QProduct;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.ProductMapper;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    @PersistenceContext
    private final EntityManager entityManager;

    private final ProductRepository productRepository;

    @Override
    public Page<ProductDto> findAllProducts(Pageable pageable) {
        Page<Product> all = productRepository.findAll(pageable);
        return ProductMapper.mapPageToDto(all);
    }

    @Override
    public Page<ProductDto> findByName(String name, Pageable pageable) {
        Page<Product> byNameContainingIgnoreCase = productRepository.findByNameContainingIgnoreCase(name, pageable);
        return ProductMapper.mapPageToDto(byNameContainingIgnoreCase);
    }

    @Override
    public List<ProductDto> findAll() {
        List<Product> all = productRepository.findAll();
        return ProductMapper.mapToListProductDto(all);
    }

    @Override
    public void remove(int id) {
        productRepository.deleteById(id);
    }


    @Override
    @Transactional
    public ProductDto save(CreateProductRequestDto productRequestDto, MultipartFile[] files, User user) throws IOException {
        Product product = ProductMapper.map(productRequestDto);
        product.getCategories().removeIf(category -> category.getId() == 0);
        List<Image> imageList = new ArrayList<>();
        product.setUser(user);
        for (MultipartFile multipartFile : files) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
                File file = new File(imageUploadPath + fileName);
                multipartFile.transferTo(file);
                Image image = new Image();
                image.setImage(fileName);
                imageList.add(image);
            }
        }
        product.setImages(imageList);
        Product save = productRepository.save(product);

        return ProductMapper.mapToDto(save);
    }

    @Override
    public ProductDto save(CreateProductRequestDto productRequestDto, User user) {
        Product product = ProductMapper.map(productRequestDto);
        product.getCategories().removeIf(category -> category.getId() == 0);
        product.setUser(user);
        Product save = productRepository.save(product);
        return ProductMapper.mapToDto(save);
    }

    @Override
    @Transactional
    public ProductDto save(int id, MultipartFile[] files) throws IOException {
        Product product = productRepository.findById(id).orElse(null);
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
                File file = new File(imageUploadPath + fileName);
                multipartFile.transferTo(file);
                Image image = new Image();
                image.setImage(fileName);
                imageList.add(image);
            }
        }
        product.setImages(imageList);
        Product save = productRepository.save(product);
        return ProductMapper.mapToDto(save);
    }


    @Override
    public ProductDto findById(int id) {
        Optional<Product> byId = productRepository.findById(id);
        return byId.map(ProductMapper::mapToDto).orElse(null);
    }

    @Override
    public List<ProductDto> search(int page, int size,
                                   FilterProductDto filterProductDto) {
        List<Product> all = searchProductByFilter(page, size, filterProductDto);

        List<ProductDto> productDtoList = ProductMapper.mapProductList(all);
        return productDtoList;
    }

    private List<Product> searchProductByFilter(int page, int size, FilterProductDto filterProductDto) {
        QProduct qProduct = QProduct.product;
        var query = new JPAQuery<Product>(entityManager);
        JPAQuery<Product> from = query.from(qProduct);

        if (filterProductDto.getSerialNumber() != null &&
                !filterProductDto.getSerialNumber().isEmpty()) {
            from.where(qProduct.productCode.contains(filterProductDto.getSerialNumber()));
        }

        if (filterProductDto.getMinPrice() > 0 && filterProductDto.getMaxPrice() > 0) {
            from.where(qProduct.price.gt(filterProductDto.getMinPrice())
                    .and(qProduct.price.lt(filterProductDto.getMaxPrice())));
        }

        if (page > 0) {
            from.offset((long) page * size);
        }
        from.limit(size);

        PathBuilder<Object> orderByExpression = new PathBuilder<Object>(QProduct.class, filterProductDto.getSortBy());

        from.orderBy(new OrderSpecifier("asc".equalsIgnoreCase(filterProductDto.getSortDirection()) ? Order.ASC
                : Order.DESC, orderByExpression));

        return from.fetch();
    }


}
