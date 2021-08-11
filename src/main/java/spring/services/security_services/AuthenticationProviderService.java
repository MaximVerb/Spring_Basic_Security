package spring.services.security_services;

import spring.config.SecureUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationProviderService implements AuthenticationProvider {
    @Autowired
    JpaUserDetailsService jpaUserDetailsService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    SCryptPasswordEncoder sCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        SecureUser secureUser = jpaUserDetailsService.loadUserByUsername(username);

        switch(secureUser.getUser().getAlgorithm()) {
            case BCRYPT:
                return checkPassword(secureUser, password, bCryptPasswordEncoder);
            case SCRYPT:
                return checkPassword(secureUser, password, sCryptPasswordEncoder);
        }

        throw new BadCredentialsException(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());
    }

    @Override
    public boolean supports(Class<?> token) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(token);
    }

    private Authentication checkPassword(SecureUser user,
                                         String rawPassword,
                                         PasswordEncoder encoder) {

        if (encoder.matches(rawPassword, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities());
        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
