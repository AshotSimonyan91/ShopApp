package am.shopappRest.shoppingApplicationRest.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This utility class provides methods for handling JWT (JSON Web Token) generation, validation, and parsing.
 * It is responsible for generating JWT tokens, refreshing tokens, and validating token authenticity and expiration.
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Extracts the subject (username) from the JWT token.
     *
     * @param token The JWT token from which to extract the subject.
     * @return The username (subject) extracted from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts the issued-at date from the JWT token.
     *
     * @param token The JWT token from which to extract the issued-at date.
     * @return The issued-at date extracted from the token.
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token from which to extract the expiration date.
     * @return The expiration date extracted from the token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using the provided claims resolver function.
     *
     * @param token         The JWT token from which to extract the claim.
     * @param claimsResolver The function to resolve the claim from the token's claims.
     * @param <T>           The type of the claim to be extracted.
     * @return The extracted claim of type T.
     */

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generates a new JWT token for the given email (subject).
     *
     * @param email The email (subject) for which to generate the token.
     * @return The generated JWT token.
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, email);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Refreshes an existing JWT token by updating its issued-at and expiration dates.
     *
     * @param token The existing JWT token to be refreshed.
     * @return The refreshed JWT token.
     */
    public String refreshToken(String token) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates the authenticity and expiration of a JWT token for the given email (subject).
     *
     * @param token The JWT token to be validated.
     * @param email The email (subject) to which the token should belong.
     * @return True if the token is valid and not expired, otherwise false.
     */
    public Boolean validateToken(String token, String email) {

        final String username = getUsernameFromToken(token);
        return (
                username.equals(email)
                        && !isTokenExpired(token));
    }

    /**
     * Calculates the expiration date for a JWT token based on the given created date and the configured token expiration time.
     *
     * @param createdDate The date on which the token was created.
     * @return The calculated expiration date for the token.
     */
    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

    /**
     * Gets the signing key used for generating and validating JWT tokens.
     *
     * @return The signing key.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
