package am.shopappRest.shoppingApplicationRest.filter;


import am.shopappRest.shoppingApplicationRest.security.CurrentUserDetailServiceImpl;
import am.shopappRest.shoppingApplicationRest.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This class is a filter that intercepts incoming HTTP requests and performs JWT (JSON Web Token) authentication for the requests.
 * It extracts the JWT token from the request header, validates it, and sets the authentication details in the SecurityContextHolder
 * for the authenticated user. The filter uses the JwtTokenUtil to extract information from the JWT token and the
 * CurrentUserDetailServiceImpl to load the user details for authentication.
 */
@Component
@RequiredArgsConstructor
public class JWTAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil tokenUtil;

    private final CurrentUserDetailServiceImpl userDetailsService;

    /**
     * This method intercepts incoming HTTP requests, extracts the JWT token from the "Authorization" header, and performs
     * JWT authentication. If the token is valid, it loads the user details from the token, creates an authentication object,
     * and sets it in the SecurityContextHolder for further authentication. Finally, it lets the request proceed to the next
     * filter in the chain.
     *
     * @param httpServletRequest  The incoming HTTP request.
     * @param httpServletResponse The outgoing HTTP response.
     * @param filterChain         The filter chain for processing the request.
     * @throws ServletException if there is an issue during the filter processing.
     * @throws IOException      if there is an issue with IO operations during the filter processing.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String requestHeader = httpServletRequest.getHeader("Authorization");

        String username = null;
        String authToken = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7);
            try {
                username = tokenUtil.getUsernameFromToken(authToken);
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (tokenUtil.validateToken(authToken, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
