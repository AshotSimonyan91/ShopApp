package am.shopappweb.shopappweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SpringSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public SpringSecurityConfig(PasswordEncoder passwordEncoder,
                                UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/products").permitAll()
                        .requestMatchers("/search").permitAll()
                        .requestMatchers("/products/list").permitAll()
                        .requestMatchers("/getImage").permitAll()
                        .requestMatchers("/user/register").permitAll()
                        .requestMatchers("/user/changePassword").permitAll()
                        .requestMatchers("/user/verify").permitAll()
                        .requestMatchers("/user/forgotPassword").permitAll()
                        .requestMatchers("/user/admin").hasAuthority("ADMIN")
                        .requestMatchers("/delivery").hasAnyAuthority("DELIVERY","ADMIN")
                        .requestMatchers("/delivery/**").hasAnyAuthority("DELIVERY","ADMIN")
                        .requestMatchers("/img/**").permitAll()
                        .requestMatchers("/assets/css/**").permitAll()
                        .requestMatchers("/assets/images").permitAll()
                        .requestMatchers("/assets/js/**").permitAll()
                        .requestMatchers("/assets/libs/**").permitAll()
                        .requestMatchers("/assets/apexcharts/**").permitAll()
                        .requestMatchers("/assets/maps/**").permitAll()
                        .requestMatchers("/assets/js/pages/**").permitAll()
                        .requestMatchers("/vendor/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/fonts/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/image/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                        .loginPage("/customLogin")
                        .defaultSuccessUrl("/customSuccessLogin",true)
                        .loginProcessingUrl("/login")
                        .permitAll())
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutSuccessUrl("/")
                        .permitAll());
        return httpSecurity.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

}