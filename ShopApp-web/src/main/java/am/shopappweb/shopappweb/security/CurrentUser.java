package am.shopappweb.shopappweb.security;


import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;
/**
 * The `CurrentUser` class extends the `org.springframework.security.core.userdetails.User` class to represent the currently authenticated user.
 * It encapsulates the user details, such as email, password, and role, and provides methods to access these details.
 */

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;
    /**
     * Constructs a new `CurrentUser` object with the given `User` entity, initializing the user details required for authentication.
     *
     * @param user The `User` entity representing the currently authenticated user.
     */

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }
    /**
     * Returns the `User` entity representing the currently authenticated user.
     *
     * @return The `User` entity of the currently authenticated user.
     */
    public User getUser() {
        return user;
    }
}