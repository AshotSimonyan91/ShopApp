package am.shopappRest.shoppingApplicationRest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@ComponentScan(basePackages ={"am.shopappRest.shoppingApplicationRest","am.shoppingCommon.shoppingApplication"})
@EntityScan(basePackages = {"am.shoppingCommon.shoppingApplication.entity"})
@EnableJpaRepositories(basePackages = {"am.shoppingCommon.shoppingApplication.repository"})
public class ShoppingApplicationRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplicationRestApplication.class, args);
    }

    @Bean
     public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
