package am.shoppingCommon.shoppingApplication.service.impl;

import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.FilterProductDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.exception.CategoryDoesNotExistsException;
import am.shoppingCommon.shoppingApplication.mapper.ProductMapper;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductReviewRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import am.shoppingCommon.shoppingApplication.util.ImageUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    @PersistenceContext
    private final EntityManager entityManager;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Page<ProductDto> findAllProducts(Pageable pageable) {
        Page<Product> all = productRepository.findAll(pageable);
        return ProductMapper.mapPageToDto(all);
    }

    @Override
    public List<ProductDto> last3ByCategory(String category) {
        List<Product> top3ByCategoriesNameOrderBOrderByIdDesc = productRepository.findTop3ByCategoriesNameOrderByIdDesc(category);
        return ProductMapper.mapProductList(top3ByCategoriesNameOrderBOrderByIdDesc);
    }

    @Override
    public List<ProductDto> findTrendingProducts() {
        List<Product> top9ByOrderByReviewDesc = productRepository.findTop9ByOrderByReviewDesc();
        return ProductMapper.mapProductList(top9ByOrderByReviewDesc);
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
    @Transactional
    public void remove(int id, User user) {
        if (user.getRole() == Role.ADMIN) {
            productRepository.deleteById(id);
            log.info("product is removed by ID: {}", id);
        }
    }


    @Override
    @Transactional
    public ProductDto save(CreateProductRequestDto productRequestDto, MultipartFile[] files, User user) throws IOException {
        validateCategories(productRequestDto);
        Product product = ProductMapper.map(productRequestDto);
        Optional<User> byId = userRepository.findById(user.getId());
        if (byId.isPresent()) {
            product.setUser(byId.get());
            List<Image> imageList = processImages(files);
            product.setImages(imageList);
        }
        product.setReview(0L);
        Product save = productRepository.save(product);
        log.info("Product is saved by ID: {}", save.getId());
        return ProductMapper.mapToDto(save);
    }

    private void validateCategories(CreateProductRequestDto productRequestDto) {
        if (productRequestDto.getCategories() == null || productRequestDto.getCategories().isEmpty()) {
            throw new CategoryDoesNotExistsException("Please add category");
        }
        productRequestDto.getCategories().removeIf(category -> category.getId() == 0);
    }

    private List<Image> processImages(MultipartFile[] files) throws IOException {
        List<Image> imageList = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                Image image = new Image();
                image.setImage(ImageUtil.imageUploadWithResize(multipartFile, imageUploadPath));
                imageList.add(image);
            }
        }
        return imageList;
    }

    @Override
    public ProductDto findById(int id, User user) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (!hasUserReviewedProductToday(product, user)) {
                saveProductReview(product, user);
            }
            return ProductMapper.mapToDto(product);
        }
        return null;
    }

    private boolean hasUserReviewedProductToday(Product product, User user) {
        if (product != null) {
            Optional<ProductReview> productReviewOptional = productReviewRepository.findByProductIdAndUserId(product.getId(), user.getId());
            if (productReviewOptional.isPresent()) {
                ProductReview productReview = productReviewOptional.get();
                return LocalDate.now().equals(productReview.getLastReview());
            }
        }
        return false;
    }

    @Async
    public void saveProductReview(Product product, User user) {
        Optional<ProductReview> productReviewOptional = productReviewRepository.findByProductIdAndUserId(product.getId(), user.getId());
        ProductReview productReview;
        if (productReviewOptional.isPresent()) {
            productReview = productReviewOptional.get();
        } else {
            productReview = new ProductReview();
            productReview.setProduct(product);
            productReview.setUser(user);
        }
        product.setReview(product.getReview() + 1);
        productReview.setLastReview(LocalDate.now());
        productRepository.save(product);
        productReviewRepository.save(productReview);
    }

    @Override
    public Page<ProductDto> findByCategory(Pageable pageable, String category) {
        Page<Product> products = productRepository.findAllByCategoriesName(category, pageable);
        return ProductMapper.mapPageToDto(products);
    }

    @Override
    public List<ProductDto> search(int page, int size,
                                   FilterProductDto filterProductDto) {
        List<Product> all = searchProductByFilter(page, size, filterProductDto);

        List<ProductDto> productDtoList = ProductMapper.mapToListProductDto(all);
        return productDtoList;
    }


    private List<Product> searchProductByFilter(int page, int size, FilterProductDto filterProductDto) {
        QProduct qProduct = QProduct.product;
        var query = new JPAQuery<Product>(entityManager);
        JPAQuery<Product> from = query.from(qProduct);

        if (filterProductDto.getMinPrice() > 0 && filterProductDto.getMaxPrice() > 0) {
            from.where(qProduct.price.gt(filterProductDto.getMinPrice())
                    .and(qProduct.price.lt(filterProductDto.getMaxPrice())));
        }
        if (filterProductDto.getProductCode() != null &&
                !filterProductDto.getProductCode().isEmpty()) {
            from.where(qProduct.productCode.contains(filterProductDto.getProductCode()));
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
