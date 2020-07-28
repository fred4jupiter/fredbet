package de.fred4jupiter.fredbet.security;

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
public class PermissionResolverImpl implements PermissionResolver {

    private final FredBetUserGroups fredBetUserGroups;

    public PermissionResolverImpl(FredBetUserGroups fredBetUserGroups) {
        this.fredBetUserGroups = fredBetUserGroups;
    }

    @Override
    public Collection<GrantedAuthority> resolvePermissions(Collection<? extends GrantedAuthority> roleAuthorities) {
        return roleAuthorities.stream().map(GrantedAuthority::getAuthority)
                .flatMap(userGroup -> fredBetUserGroups.getPermissionsForUserGroup(userGroup).stream()).collect(Collectors.toSet());
    }
}
