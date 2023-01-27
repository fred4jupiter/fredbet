package de.fred4jupiter.fredbet.security;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementing class for resolving roles to permissions.
 *
 * @author michael
 */
@Component
class PermissionResolverImpl implements PermissionResolver {

    private final FredbetProperties fredbetProperties;

    public PermissionResolverImpl(FredbetProperties fredbetProperties) {
        this.fredbetProperties = fredbetProperties;
    }

    @Override
    public Collection<GrantedAuthority> resolvePermissions(Collection<? extends GrantedAuthority> roleAuthorities) {
        return roleAuthorities.stream().map(GrantedAuthority::getAuthority)
                .flatMap(userGroup -> fredbetProperties.getPermissionsForUserGroup(userGroup).stream()).collect(Collectors.toSet());
    }
}
