package am.shopappRest.shoppingApplicationRest.security;


import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class implements the Spring Security UserDetailsService interface and is responsible for loading the user details
 * during the authentication process. It retrieves user information from the UserRepository and creates a CurrentUser object,
 * which extends the Spring Security's UserDetails class, to represent the authenticated user with additional application-specific
 * details.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads the user details by querying the UserRepository based on the given username (email). If the user is found, it creates
     * a CurrentUser object representing the authenticated user with the retrieved user details. Otherwise, it throws a UsernameNotFoundException.
     *
     * @param s The username (email) of the user to be loaded.
     * @return An implementation of the UserDetails interface representing the authenticated user.
     * @throws UsernameNotFoundException If the user with the given username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(s);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        return new CurrentUser(user.get());
    }
}
