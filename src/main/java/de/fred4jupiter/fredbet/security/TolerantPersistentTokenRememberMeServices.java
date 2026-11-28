package de.fred4jupiter.fredbet.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

/**
 * Remember-me services that send the user to the login page instead of an error page if the remember-me
 * cookie is outdated.
 * <p>
 * Each auto-login replaces the token of the cookie. If two requests with the same old cookie arrive at the
 * same time (e.g. several requests of the installed app after the session has expired) or the browser
 * misses the response with the new cookie, the next auto-login presents an outdated token. Spring Security
 * then throws a {@link CookieTheftException}, which ends up as an error page. The stored tokens of the user
 * are already deleted at this point, so the cookie is simply dropped and the user has to log in again.
 */
public class TolerantPersistentTokenRememberMeServices extends PersistentTokenBasedRememberMeServices {

    private static final Logger LOG = LoggerFactory.getLogger(TolerantPersistentTokenRememberMeServices.class);

    public TolerantPersistentTokenRememberMeServices(String key, UserDetailsService userDetailsService,
                                                     PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                                 HttpServletResponse response) {
        try {
            return super.processAutoLoginCookie(cookieTokens, request, response);
        } catch (CookieTheftException e) {
            LOG.warn("Outdated remember-me cookie, the user has to log in again: {}", e.getMessage());
            // a RememberMeAuthenticationException lets the caller delete the cookie and continue without login
            throw new RememberMeAuthenticationException(e.getMessage());
        }
    }
}
