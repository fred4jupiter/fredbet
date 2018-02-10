package de.fred4jupiter.fredbet.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.fred4jupiter.fredbet.domain.AppUser;

/**
 * Will be created in PermissionResolverPostProcessor.
 * 
 * @author michael
 *
 */
public class UserDetailsServicePermissionDecorator implements UserDetailsService {

    private final UserDetailsService userDetailsService;

    private final PermissionResolver permissionResolver;

    public UserDetailsServicePermissionDecorator(UserDetailsService userDetailsService, PermissionResolver permissionResolver) {
        this.userDetailsService = userDetailsService;
        this.permissionResolver = permissionResolver;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (!(userDetails instanceof AppUser)) {
            throw new IllegalStateException("Resolved UserDetails is not of type AppUser!");
        }

        AppUser appUser = (AppUser) userDetails;

        Collection<GrantedAuthority> permissions = permissionResolver.resolvePermissions(appUser.getAuthorities());

        appUser.setRoles(toRoles(permissions));

        return appUser;
    }

    private Set<String> toRoles(Collection<GrantedAuthority> permissions) {
        return permissions.stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toSet());
    }

}
