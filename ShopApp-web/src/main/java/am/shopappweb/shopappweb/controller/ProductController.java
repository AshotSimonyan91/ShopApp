package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * The ProductController class handles HTTP requests related to products and product pages.
 * It provides functionality to view products, product details, add new products, and remove products.
 * The controller interacts with the ProductService, CategoryService, and CommentService to perform
 * business logic and retrieve data.
 */
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CommentService commentService;


    /**
     * Handles the HTTP GET request to view the product page ("/products").
     * Retrieves paginated product data from the ProductService and populates the modelMap
     * with the necessary information for display in the view.
     *
     * @param modelMap The ModelMap to be populated with product data for the view.
     * @param page     Optional parameter for the current page number.
     * @param size     Optional parameter for the number of products per page.
     * @return The view name "products" to display the product page.
     */
    @GetMapping
    public String productPage(ModelMap modelMap,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<ProductDto> result = productService.findAllProducts(pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("products", result);
        return "products";
    }


    /**
     * Handles the HTTP GET request to view the product list page ("/products/list").
     * This method is similar to "productPage" but renders a different view for the product list.
     *
     * @param modelMap The ModelMap to be populated with product data for the view.
     * @param page     Optional parameter for the current page number.
     * @param size     Optional parameter for the number of products per page.
     * @return The view name "products-list" to display the product list page.
     */
    @GetMapping("/list")
    public String productListPage(ModelMap modelMap,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<ProductDto> result = productService.findAllProducts(pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("products", result);
        return "products-list";
    }


    /**
     * Handles the HTTP GET request to view the details of a specific product ("/products/{id}").
     * Retrieves product details, trending products, and comments related to the specified product
     * from the ProductService and CommentService, and populates the modelMap for display in the view.
     *
     * @param id          The ID of the product to view.
     * @param currentUser The currently authenticated user containing user information.
     * @param modelmap    The ModelMap to be populated with product data and comments for the view.
     * @return The view name "singleProductPage" to display the product details page.
     */
    @GetMapping("{id}")
    public String currentProductPage(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser, ModelMap modelmap) {
        modelmap.addAttribute("currentProduct", productService.findById(id, currentUser.getUser()));
        modelmap.addAttribute("products", productService.findTrendingProducts());
        modelmap.addAttribute("comments", commentService.findAllByLimit(id));
        return "singleProductPage";
    }


    /**
     * Handles the HTTP GET request to view products by category ("/products/search/{category}").
     * Retrieves paginated product data of the specified category from the ProductService
     * and populates the modelMap for display in the view.
     *
     * @param modelMap The ModelMap to be populated with product data for the view.
     * @param category The name of the category to filter products.
     * @param page     Optional parameter for the current page number.
     * @param size     Optional parameter for the number of products per page.
     * @return The view name "products" to display the products filtered by category.
     */
    @GetMapping("/search/{category}")
    public String getProductByCategoryName(ModelMap modelMap, @PathVariable("category") String category,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<ProductDto> result = productService.findByCategory(pageable, category);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("products", result);
        return "products";
    }


    /**
     * Handles the HTTP GET request to view the "add product" page ("/products/add").
     * Retrieves category data from the CategoryService and populates the modelMap for display in the view.
     *
     * @param modelMap The ModelMap to be populated with category data for the view.
     * @return The view name "addProducts" to display the "add product" page.
     */
    @GetMapping("/add")
    public String addProductPage(ModelMap modelMap) {
        modelMap.addAttribute("categories", categoryService.findAllCategory());
        return "addProducts";
    }

    /**
     * Handles the HTTP POST request to add a new product ("/products/add").
     * Saves a new product using the ProductService based on the provided form data and files,
     * and redirects back to the "add product" page ("/admin/add/product").
     *
     * @param createProductRequestDto The CreateProductRequestDto containing product details.
     * @param currentUser             The currently authenticated user containing user information.
     * @param files                   The array of MultipartFile objects representing product images.
     * @return A redirection to the "add product" page.
     * @throws IOException If there is an error while processing image files.
     */
    @PostMapping("/add")
    public String addProduct(@ModelAttribute CreateProductRequestDto createProductRequestDto,
                             @AuthenticationPrincipal CurrentUser currentUser,
                             @RequestParam("files") MultipartFile[] files) throws IOException {
        productService.save(createProductRequestDto, files, currentUser.getUser());
        return "redirect:/admin/add/product";
    }


    /**
     * Handles the HTTP GET request to remove a product ("/products/remove").
     * Removes the specified product using the ProductService based on the provided product ID
     * and the currently authenticated user, and redirects back to the product page ("/products").
     *
     * @param id          The ID of the product to be removed.
     * @param currentUser The currently authenticated user containing user information.
     * @return A redirection to the product page.
     */
    @GetMapping("/remove")
    public String removeProduct(@RequestParam("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        productService.remove(id, currentUser.getUser());
        return "redirect:/products";
    }

}
