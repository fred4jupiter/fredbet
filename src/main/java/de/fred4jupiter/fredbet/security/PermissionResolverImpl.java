package de.fred4jupiter.fredbet.security;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Implementing class for resolving roles to permissions.
 * 
 * @author michael
 *
 */
@Component
public class PermissionResolverImpl implements PermissionResolver {

	@Override
	public Collection<GrantedAuthority> resolvePermissions(Collection<? extends GrantedAuthority> roleAuthorities) {
		final Collection<GrantedAuthority> allPermissions = new HashSet<>();

		for (GrantedAuthority grantedAuthority : roleAuthorities) {
			final String role = grantedAuthority.getAuthority();
			Collection<? extends GrantedAuthority> permissionForRole = FredBetRole.fromRole(role).getPermissions();
			if (!CollectionUtils.isEmpty(permissionForRole)) {
				allPermissions.addAll(permissionForRole);
			}
		}
		return allPermissions;
	}

}
