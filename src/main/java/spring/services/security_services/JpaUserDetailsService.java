package spring.services.security_services;

import spring.config.SecureUser;
import spring.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.repositories.UserRepository;

import java.util.function.Supplier;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public SecureUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> userExe =
                () -> new UsernameNotFoundException(HttpStatus.NOT_FOUND.getReasonPhrase());
        User u = userRepository
                .findUserByUsername(username)
                .orElseThrow(userExe);

        return new SecureUser(u);
    }
}
