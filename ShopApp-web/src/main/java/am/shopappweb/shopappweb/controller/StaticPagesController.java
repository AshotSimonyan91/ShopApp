package am.shopappweb.shopappweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
/**
 * The StaticPagesController class handles HTTP requests related to static pages of the website.
 * It provides methods to return the view names for various pages like the blog, about, contact us, FAQ,
 * policy, and support pages.
 */
public class StaticPagesController {

    @GetMapping("/blog")
    public String blogPage() {
        return "blog/blog";
    }

    @GetMapping("/blog-single")
    public String blogSinglePage() {
        return "blog/blog-single";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "pages/about";
    }

    @GetMapping("/contact-us")
    public String contactUs() {
        return "pages/contact-us";
    }

    @GetMapping("/faq")
    public String faq() {
        return "pages/faq";
    }

    @GetMapping("/policy")
    public String policy() {
        return "pages/policy";
    }

    @GetMapping("/support")
    public String support() {
        return "pages/support";
    }

    @GetMapping("/support-topic")
    public String supportTopic() {
        return "pages/support-topic";
    }
}
