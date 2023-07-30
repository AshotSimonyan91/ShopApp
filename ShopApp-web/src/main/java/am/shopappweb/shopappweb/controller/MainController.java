package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.productDto.ProductDto;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.MainService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The MainController class is responsible for handling HTTP requests related to the main page,
 * search functionality, and custom login. It serves as the main entry point for user interactions
 * with the web application and interacts with the MainService, CategoryService, and ProductService
 * to retrieve data and perform business logic.
 */
@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    private final CategoryService categoryService;
    private final ProductService productService;


    /**
     * Handles the HTTP GET request to the main page ("/"). Retrieves trending products,
     * categories, and new products from the corresponding services and populates the modelmap
     * to be displayed in the view.
     *
     * @param modelmap The ModelMap to be populated with data for the main page view.
     * @return The view name "index" to be displayed as the main page.
     */
    @GetMapping("/")
    public String main(ModelMap modelmap) {
        modelmap.addAttribute("trendingProducts", productService.findTrendingProducts());

        modelmap.addAttribute("toys", categoryService.findByParent("toys"));
        modelmap.addAttribute("giftForMens", categoryService.findByParent("giftForMens"));
        modelmap.addAttribute("giftForWomen", categoryService.findByParent("giftForWomen"));
        modelmap.addAttribute("watches", categoryService.findByParent("watches"));

        modelmap.addAttribute("laptop", categoryService.findByParent("laptop"));
        modelmap.addAttribute("phones", categoryService.findByParent("phone"));
        modelmap.addAttribute("tv", categoryService.findByParent("tv"));
        modelmap.addAttribute("lights", categoryService.findByParent("lights"));

        modelmap.addAttribute("women", categoryService.findByParent("women"));
        modelmap.addAttribute("livingRoom", categoryService.findByParent("livingRoom"));

        modelmap.addAttribute("newTvs", productService.last3ByCategory("TV"));
        modelmap.addAttribute("newPhones", productService.last3ByCategory("iPhone"));
        modelmap.addAttribute("newHeadPhones", productService.last3ByCategory("headphone"));

        return "index";
    }


    /**
     * Handles the HTTP GET request to retrieve an image based on the provided imageName.
     * This endpoint is used to display profile pictures or other images in the web application.
     *
     * @param imageName The name of the image to be retrieved.
     * @return The byte array of the image as a response body.
     * @throws IOException If there is an error while reading the image.
     */
    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("profilePic") String imageName) throws IOException {
        return mainService.getImage(imageName);
    }


    /**
     * Handles the HTTP GET request to the custom login page. It serves as a custom login page
     * for user authentication.
     *
     * @return The view name "customLoginPage" to be displayed for custom login.
     */
    @GetMapping("/customLogin")
    public String customLogin() {
        return "customLoginPage";
    }


    /**
     * Handles the HTTP GET request after a successful login. Redirects the user based on their role
     * (ADMIN, USER, or DELIVERY) to their respective pages.
     *
     * @param currentUser The currently authenticated user containing role information.
     * @return A redirection to the appropriate page based on the user's role.
     */
    @GetMapping("/customSuccessLogin")
    public String customSuccessLogin(@AuthenticationPrincipal CurrentUser currentUser) {
        User user = currentUser.getUser();
        if (user != null) {
            if (user.getRole() == Role.ADMIN) {
                return "redirect:/admin";
            } else if (user.getRole() == Role.USER) {
                return "redirect:/";
            } else if (user.getRole() == Role.DELIVERY) {
                return "redirect:/delivery";
            }
        }
        return "redirect:/";
    }


    /**
     * Handles the HTTP GET request to the search page ("/search") for product search functionality.
     * Retrieves products matching the search value and paginates the results.
     *
     * @param value    The search value entered by the user.
     * @param page     The current page number for pagination.
     * @param size     The number of products to be displayed per page.
     * @param modelMap The ModelMap to be populated with search results for the view.
     * @return The view name "result" to display the search results page.
     */
    @GetMapping("/search")
    public String searchPage(@RequestParam("value") String value,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "9") Integer size,
                             ModelMap modelMap) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductDto> result = productService.findByName(value, pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().toList();
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", page);
        modelMap.addAttribute("products", result);
        modelMap.addAttribute("value", value);
        return "result";
    }

}