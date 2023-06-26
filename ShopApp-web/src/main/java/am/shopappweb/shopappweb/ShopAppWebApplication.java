package am.shopappweb.shopappweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages ={"am.shopappweb.shopappweb","am.shoppingCommon.shoppingApplication"})
@EntityScan(basePackages = {"am.shoppingCommon.shoppingApplication.entity"})
@EnableJpaRepositories(basePackages = {"am.shoppingCommon.shoppingApplication.repository"})
public class ShopAppWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopAppWebApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
