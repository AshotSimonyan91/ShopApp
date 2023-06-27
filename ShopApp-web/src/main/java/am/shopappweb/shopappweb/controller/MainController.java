package am.shopappweb.shopappweb.controller;


import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.MainService;
import am.shoppingCommon.shoppingApplication.service.ProductService;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
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
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/")
    public String main(ModelMap modelmap) {
        Map<String, List<CategoryDto>> parentCategoriesMap = categoryService.getParentCategoriesWithChildren();
        modelmap.addAttribute("parentCategoriesMap", parentCategoriesMap);
        return "index";
    }

    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("profilePic") String imageName) throws IOException {
        return mainService.getImage(imageName);
    }

    @GetMapping("/customLogin")
    public String customLogin() {
        return "customLoginPage";
    }

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

    @GetMapping("/search")
    public String searchPage(@RequestParam("value") String value,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "2") Integer size, ModelMap modelMap) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> result = productService.findByName(value, pageable);
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