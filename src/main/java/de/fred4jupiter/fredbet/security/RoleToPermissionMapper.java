package de.fred4jupiter.fredbet.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class RoleToPermissionMapper {

	private static final Collection<GrantedAuthority> PERMISSIONS_ROLE_USER = new HashSet<>();

	private static final Collection<GrantedAuthority> PERMISSIONS_ROLE_USER_ADVANCED = new HashSet<>();

	private static final Collection<GrantedAuthority> PERMISSIONS_ROLE_ADMIN = new HashSet<>();

	@PostConstruct
	private void init() {
	    // User Advanced
		PERMISSIONS_ROLE_USER_ADVANCED.add(new SimpleGrantedAuthority(FredBetPermission.PERM_EDIT_MATCH_RESULT));

		// User Admin
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_CREATE_MATCH));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_EDIT_MATCH));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_EDIT_MATCH_RESULT));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_DELETE_MATCH));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_CREATE_USER));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_DELETE_USER));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_PASSWORD_RESET));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_USER_ADMINISTRATION));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_BUILD_INFO));
		PERMISSIONS_ROLE_ADMIN.add(new SimpleGrantedAuthority(FredBetPermission.PERM_ADMINISTRATION));
	}

	public Collection<? extends GrantedAuthority> getPermissionForRole(FredBetRole role) {
		if (FredBetRole.ROLE_USER.equals(role)) {
			return PERMISSIONS_ROLE_USER;
		}

		if (FredBetRole.ROLE_USER_ADVANCED.equals(role)) {
			return PERMISSIONS_ROLE_USER_ADVANCED;
		}

		if (FredBetRole.ROLE_ADMIN.equals(role)) {
			return PERMISSIONS_ROLE_ADMIN;
		}
		
		return Collections.emptyList();
	}
}
