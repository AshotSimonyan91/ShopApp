package am.shopappweb.shopappweb.controller;


import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.CommentService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping
    public String productPage(ModelMap modelMap,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Product> result = productService.findAllProducts(pageable);
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

    @GetMapping("/list")
    public String productListPage(ModelMap modelMap,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Product> result = productService.findAllProducts(pageable);
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

    @GetMapping("{id}")
    public String currentProductPage(ModelMap modelmap,
                                     @PathVariable("id") int id) {
        modelmap.addAttribute("currentProduct", productService.findById(id));
        modelmap.addAttribute("products", productService.findAll());
        modelmap.addAttribute("comments", commentService.findAllByLimit(id));
        return "singleProductPage";
    }

    @GetMapping("/add")
    public String addProductPage(ModelMap modelMap) {
        List<CategoryDto> allCategory = categoryService.findAllCategory();
        modelMap.addAttribute("categories", allCategory);
        return "addProducts";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute CreateProductRequestDto createProductRequestDto,
                             @AuthenticationPrincipal CurrentUser currentUser,
                             @RequestParam("files") MultipartFile[] files) throws IOException {
        productService.save(createProductRequestDto, files, currentUser);
        return "redirect:/";
    }

    @GetMapping("/remove")
    public String removeProduct(@RequestParam("id") int id) {
        productService.remove(id);
        return "redirect:/products";
    }

}
