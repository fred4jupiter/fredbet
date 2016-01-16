package de.fred4jupiter.fredbet.security;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.FredBetPermission;
import de.fred4jupiter.fredbet.FredBetRole;

@Component
public class PermissionResolverImpl implements PermissionResolver {

    @Override
    public Collection<GrantedAuthority> resolvePermissions(Collection<? extends GrantedAuthority> roleAuthorities) {
        final Collection<GrantedAuthority> allPermissions = new HashSet<>();
        
        for (GrantedAuthority grantedAuthority : roleAuthorities) {
            final String role = grantedAuthority.getAuthority();
            Collection<? extends GrantedAuthority> permissionForRole = getPermissionForRole(role);
            if (!CollectionUtils.isEmpty(permissionForRole)) {
                allPermissions.addAll(permissionForRole);    
            }
        }
        return allPermissions;
    }

    private Collection<? extends GrantedAuthority> getPermissionForRole(String role) {
        final Collection<GrantedAuthority> permissionsForRole = new HashSet<>();
        if (FredBetRole.ROLE_USER.name().equals(role)) {
            return permissionsForRole;
        }
        
        if (FredBetRole.ROLE_USER_ADVANCED.name().equals(role)) {
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_EDIT_MATCH));
            return permissionsForRole;
        }
        
        if (FredBetRole.ROLE_ADMIN.name().equals(role)) {
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_CREATE_MATCH));
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_EDIT_MATCH));
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_DELETE_MATCH));

            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_CREATE_USER));
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_DELETE_USER));
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_PASSWORD_RESET));
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_USER_ADMINISTRATION));
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_BUILD_INFO));
            permissionsForRole.add(new SimpleGrantedAuthority(FredBetPermission.PERM_ADMINISTRATION));
            return permissionsForRole;
        }
        
        return null;
    }

}
