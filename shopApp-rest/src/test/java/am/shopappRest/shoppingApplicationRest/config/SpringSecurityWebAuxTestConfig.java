package am.shopappRest.shoppingApplicationRest.config;


import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User basicUser = new User(1,"Basic User", "Surname ","user@shopApp.com", "password",null,null, Role.USER,null,true,null,null);
        CurrentUser currentUser = new CurrentUser(basicUser);

        return new InMemoryUserDetailsManager(Arrays.asList(
                currentUser
        ));
    }
}