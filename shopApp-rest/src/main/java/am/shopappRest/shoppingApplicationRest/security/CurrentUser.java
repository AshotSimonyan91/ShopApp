package am.shopappRest.shoppingApplicationRest.security;

import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * This class represents the currently authenticated user in the application. It extends the Spring Security's User class,
 * which provides the necessary details for user authentication. Additionally, this class includes a reference to the
 * User entity from the application domain model, allowing easy access to the user's additional information.
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    /**
     * Returns the User entity associated with the currently authenticated user.
     *
     * @return The User entity representing the currently authenticated user.
     */
    public User getUser() {
        return user;
    }

}
