package de.fred4jupiter.fredbet.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static de.fred4jupiter.fredbet.security.FredBetPermission.*;

/**
 * Fredbet roles.
 * 
 * @author michael
 *
 */
public enum FredBetRole {

	/**
	 * Normal user role with permission to bet matches.
	 */
	ROLE_USER("msg.ROLE_USER"),

	/**
	 * Like the user role but with permission to enter the results of a match.
	 */
	ROLE_USER_ENTER_RESULTS("msg.ROLE_USER_ENTER_RESULTS", PERM_EDIT_MATCH_RESULT),

	/**
	 * Like ROLE_USER_ENTER_RESULTS role but with permission to add users.
	 */
	ROLE_SUB_ADMIN("msg.ROLE_USER_ENTER_RESULTS_ADD_USERS", PERM_EDIT_MATCH, PERM_EDIT_MATCH_RESULT, PERM_CREATE_USER,
			PERM_USER_ADMINISTRATION, PERM_PASSWORD_RESET, PERM_EDIT_USER, PERM_EDIT_INFOS_RULES, PERM_EDIT_INFOS_PRICES),

	/**
	 * All permissions.
	 */
	ROLE_ADMIN("msg.ROLE_ADMIN", PERM_CREATE_MATCH, PERM_EDIT_MATCH, PERM_EDIT_MATCH_RESULT, PERM_DELETE_MATCH, PERM_CREATE_USER,
			PERM_EDIT_USER, PERM_DELETE_USER, PERM_PASSWORD_RESET, PERM_USER_ADMINISTRATION, PERM_SYSTEM_INFO, PERM_ADMINISTRATION,
			PERM_CHANGE_USER_ROLE, PERM_EDIT_INFOS_RULES, PERM_EDIT_INFOS_PRICES, PERM_SHOW_ACTIVE_USERS, PERM_SHOW_LAST_LOGINS,
			PERM_EDIT_IMAGE_GROUP, PERM_DOWNLOAD_IMAGES, PERM_DELETE_ALL_IMAGES);

	private String[] permissions = new String[] {};

	private String msgUserRole;

	private FredBetRole(String msgUserRole, String... permissions) {
		this.msgUserRole = msgUserRole;
		this.permissions = permissions;
	}

	public String getMsgUserRole() {
		return msgUserRole;
	}

	public Collection<? extends GrantedAuthority> getPermissions() {
		return Arrays.asList(permissions).stream().map(permission -> new SimpleGrantedAuthority(permission)).collect(Collectors.toList());
	}

	public static FredBetRole fromRole(String roleName) {
		FredBetRole[] values = values();
		for (FredBetRole fredBetRole : values) {
			if (fredBetRole.name().equals(roleName)) {
				return fredBetRole;
			}
		}

		throw new IllegalArgumentException("Unknown role name " + roleName);
	}
}
