package de.fred4jupiter.fredbet.common;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * Creates an HTTP session with a logged-in user for MockMvc requests.
 */
public final class LoggedInSession {

    private LoggedInSession() {
    }

    public static MockHttpSession of(UserDetailsService userDetailsService, String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        var authentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(authentication));
        return session;
    }
}
