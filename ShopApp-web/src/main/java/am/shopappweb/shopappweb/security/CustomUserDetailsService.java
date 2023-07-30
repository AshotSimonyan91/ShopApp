package am.shopappweb.shopappweb.security;


import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
/**
 * The `CustomUserDetailsService` class is an implementation of the Spring Security `UserDetailsService` interface.
 * It is responsible for loading user details for authentication from the UserRepository based on the provided username (email).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Loads the user details from the UserRepository based on the provided username (email).
     *
     * @param username The username (email) of the user to load.
     * @return The `CurrentUser` object representing the user with the provided username.
     * @throws UsernameNotFoundException If no user is found with the given username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> byEmail = userRepository.findByEmail(username);
        if (byEmail.isEmpty()) {
            throw new UsernameNotFoundException("User does not exists");
        }
        return new CurrentUser(byEmail.get());
    }
}