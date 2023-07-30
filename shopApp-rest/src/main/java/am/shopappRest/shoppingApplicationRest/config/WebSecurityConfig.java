package am.shopappRest.shoppingApplicationRest.config;


import am.shopappRest.shoppingApplicationRest.security.JwtAuthenticationEntryPoint;
import am.shopappRest.shoppingApplicationRest.filter.JWTAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This configuration class provides the setup for securing the REST API endpoints in the shopping application
 * using Spring Security. It defines the security filter chain, authentication entry point, and the necessary
 * authentication provider. The class disables CSRF protection, sets up stateless session management,
 * and configures access rules for different endpoints. Additionally, it registers a JWTAuthenticationTokenFilter
 * to handle JWT authentication for API requests.
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JWTAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * Configures the security filter chain for handling HTTP security in the shopping application. It sets up various
     * security configurations such as disabling CSRF protection, stateless session management, and access rules for
     * different API endpoints. The JWTAuthenticationTokenFilter is registered to handle JWT authentication for API requests.
     *
     * @param httpSecurity The HttpSecurity object used to configure the security filter chain.
     * @return The SecurityFilterChain representing the configured security filter chain.
     * @throws Exception If there's an issue with configuring the security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(HttpMethod.POST, "/user/auth").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/register").permitAll()
                        .requestMatchers("/user/forgotPassword").permitAll()
                        .requestMatchers("/user/changePassword").permitAll()
                        .requestMatchers("/getImage/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated());

        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * Creates and configures an AuthenticationProvider for handling user authentication. It sets the UserDetailsService
     * and PasswordEncoder for the authentication provider.
     *
     * @return The configured AuthenticationProvider for user authentication.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }


}
