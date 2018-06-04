package de.fred4jupiter.fredbet.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

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
		return roleAuthorities.stream().map(GrantedAuthority::getAuthority)
				.flatMap(role -> FredBetRole.fromRole(role).getPermissions().stream())
				.collect(Collectors.toSet());
	}

}
