package am.shopappRest.shoppingApplicationRest.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

/**
 * This class implements the Spring Security AuthenticationEntryPoint interface and is responsible for handling authentication errors.
 * When an unauthenticated user tries to access a protected resource, the JwtAuthenticationEntryPoint will be triggered, and it will send
 * an HTTP 401 Unauthorized response back to the client.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
     * This method is called whenever an unauthenticated user tries to access a protected resource. It sends an HTTP 401 Unauthorized response
     * back to the client, indicating that the requested resource requires authentication.
     *
     * @param request       The HttpServletRequest object representing the client request.
     * @param response      The HttpServletResponse object representing the server response.
     * @param authException The AuthenticationException representing the authentication error that occurred.
     * @throws IOException If an I/O error occurs while sending the response.
     *//**
     * This method is called whenever an unauthenticated user tries to access a protected resource. It sends an HTTP 401 Unauthorized response
     * back to the client, indicating that the requested resource requires authentication.
     *
     * @param request       The HttpServletRequest object representing the client request.
     * @param response      The HttpServletResponse object representing the server response.
     * @param authException The AuthenticationException representing the authentication error that occurred.
     * @throws IOException If an I/O error occurs while sending the response.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
