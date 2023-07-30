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

/**
 * Service implementation class responsible for handling product-related operations.
 * It implements the ProductService interface and provides functionalities for managing products,
 * such as fetching all products, finding trending products, searching products by name, etc.
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


    /**
     * Retrieves a page of products using pagination.
     *
     * @param pageable The Pageable object containing pagination information.
     * @return A Page of ProductDto objects.
     */
    @Override
    public Page<ProductDto> findAllProducts(Pageable pageable) {
        Page<Product> all = productRepository.findAll(pageable);
        log.info("Get all products");
        return ProductMapper.mapPageToDto(all);
    }


    /**
     * Retrieves the last 3 products of a specific category ordered by ID in descending order.
     *
     * @param category The category name for which the products are retrieved.
     * @return A list of ProductDto objects representing the last 3 products in the given category.
     */
    @Override
    public List<ProductDto> last3ByCategory(String category) {
        List<Product> top3ByCategoriesNameOrderBOrderByIdDesc = productRepository.findTop3ByCategoriesNameOrderByIdDesc(category);
        log.info("Get last 3 products by {} category",category);
        return ProductMapper.mapProductList(top3ByCategoriesNameOrderBOrderByIdDesc);
    }


    /**
     * Retrieves the top 9 trending products ordered by review score in descending order.
     *
     * @return A list of ProductDto objects representing the top 9 trending products.
     */
    @Override
    public List<ProductDto> findTrendingProducts() {
        List<Product> top9ByOrderByReviewDesc = productRepository.findTop9ByOrderByReviewDesc();
        log.info("Get products to trending");
        return ProductMapper.mapProductList(top9ByOrderByReviewDesc);
    }


    /**
     * Searches for products by name using pagination.
     *
     * @param name     The product name or part of it to be searched.
     * @param pageable The Pageable object containing pagination information.
     * @return A Page of ProductDto objects that match the search criteria.
     */
    @Override
    public Page<ProductDto> findByName(String name, Pageable pageable) {
        Page<Product> byNameContainingIgnoreCase = productRepository.findByNameContainingIgnoreCase(name, pageable);
        log.info("Get products by {} name",name);
        return ProductMapper.mapPageToDto(byNameContainingIgnoreCase);
    }


    /**
     * Retrieves all products from the database.
     *
     * @return A list of all ProductDto objects representing all products.
     */
    @Override
    public List<ProductDto> findAll() {
        List<Product> all = productRepository.findAll();
        log.info("Get all products");
        return ProductMapper.mapToListProductDto(all);
    }


    /**
     * Removes a product from the database by ID if the user's role is ADMIN.
     * This method is transactional.
     *
     * @param id   The ID of the product to be removed.
     * @param user The User object representing the user performing the action.
     */
    @Override
    @Transactional
    public void remove(int id, User user) {
        if (user.getRole() == Role.ADMIN) {
            productRepository.deleteById(id);
            log.info("product by ID: {} was removed ", id);
        }
    }


    /**
     * Save a new product in the database.
     *
     * @param productRequestDto The DTO containing the product details.
     * @param files             An array of MultipartFiles containing product images.
     * @param user              The user creating the product.
     * @return The saved product as a ProductDto.
     * @throws IOException If there is an error while processing the images.
     */
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
            log.info("Category not exist");
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

    /**
     * Retrieve a product by its ID and user. If the product is found, check if the
     * user has reviewed the product today. If not, asynchronously save the product
     * review.
     *
     * @param id   The ID of the product to retrieve.
     * @param user The user accessing the product.
     * @return The found product as a ProductDto, or null if not found.
     */
    @Override
    public ProductDto findById(int id, User user) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (!hasUserReviewedProductToday(product, user)) {
                saveProductReview(product, user);
            }
            log.info("Get product by {} id",id);
            return ProductMapper.mapToDto(product);
        }
        log.info("Product by {} id did not exist",id);
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

    /**
     * Asynchronously saves a product review for the given product and user. If the user has not reviewed the product
     * today, a new ProductReview entity is created and associated with the product and user. Otherwise, the existing
     * ProductReview entity is updated to mark today's review date.
     *
     * @param product The product to review.
     * @param user    The user writing the review.
     */
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
        log.info("User by {} id was looked product",user.getId());
    }


    /**
     * Retrieves a page of products from the database that belong to the specified category.
     *
     * @param pageable The pageable object used to control pagination.
     * @param category The name of the category to filter products by.
     * @return A Page of ProductDtos containing the products in the specified category.
     */
    @Override
    public Page<ProductDto> findByCategory(Pageable pageable, String category) {
        Page<Product> products = productRepository.findAllByCategoriesName(category, pageable);
        log.info("Get products by {} category",category);
        return ProductMapper.mapPageToDto(products);
    }


    /**
     * Searches for products based on filter criteria provided in the FilterProductDto.
     *
     * @param page             The page number for pagination.
     * @param size             The number of products per page.
     * @param filterProductDto The filter criteria used to search for products.
     * @return A List of ProductDtos containing the search results.
     */
    @Override
    public List<ProductDto> search(int page, int size,
                                   FilterProductDto filterProductDto) {
        List<Product> all = searchProductByFilter(page, size, filterProductDto);

        List<ProductDto> productDtoList = ProductMapper.mapToListProductDto(all);
        log.info("Get products by criteria");
        return productDtoList;
    }


    /**
     * Performs a filtered search for products based on the provided filter criteria.
     *
     * @param page             The page number for pagination.
     * @param size             The number of products per page.
     * @param filterProductDto The filter criteria used to search for products.
     * @return A List of Product entities containing the search results.
     */
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
