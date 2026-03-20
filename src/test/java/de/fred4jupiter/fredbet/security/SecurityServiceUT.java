package de.fred4jupiter.fredbet.security;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
class SecurityServiceUT {

    private SecurityService securityService = new SecurityService();
    private SecurityContext originalContext;

    @BeforeEach
    void saveContext() {
        originalContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void restoreContext() {
        // restore original context to avoid side effects between tests
        SecurityContextHolder.setContext(originalContext);
    }

    @Test
    void isUserLoggedIn_whenAuthenticatedAndPrincipalIsAppUser_returnsTrue() {
        AppUser user = new AppUser();
        user.setUsername("alice");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(securityService.isUserLoggedIn()).isTrue();
    }

    @Test
    void isUserLoggedIn_whenNoAuthentication_returnsFalse() {
        SecurityContextHolder.clearContext();

        assertThat(securityService.isUserLoggedIn()).isFalse();
    }

    @Test
    void getCurrentUserName_returnsUsername() {
        AppUser user = new AppUser();
        user.setUsername("bob");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(securityService.getCurrentUserName()).isEqualTo("bob");
    }

    @Test
    void isCurrentUserHavingPermission_checksPermission() {
        AppUser user = new AppUser();
        user.setUsername("permUser");
        user.setRoles(Set.of("SOME_PERMISSION"));

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(securityService.isCurrentUserHavingPermission("SOME_PERMISSION")).isTrue();
        assertThat(securityService.isCurrentUserHavingPermission("OTHER")).isFalse();
    }

    @Test
    void resetFirstLogin_updatesProvidedAndCurrentUserIfPresent() {
        AppUser provided = new AppUser();
        provided.setFirstLogin(true);

        AppUser current = new AppUser();
        current.setFirstLogin(true);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(current);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        securityService.resetFirstLogin(provided);

        assertThat(provided.isFirstLogin()).isFalse();
        assertThat(current.isFirstLogin()).isFalse();
    }

    @Test
    void resetFirstLogin_whenNoCurrentUser_onlyUpdatesProvided() {
        AppUser provided = new AppUser();
        provided.setFirstLogin(true);

        SecurityContextHolder.clearContext();

        securityService.resetFirstLogin(provided);

        assertThat(provided.isFirstLogin()).isFalse();
    }
}
