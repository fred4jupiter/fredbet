package de.fred4jupiter.fredbet.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Aufloesung von Rollen in ein Array von Rechten
 *
 * @author mis
 */
interface PermissionResolver {

    public static final String PERMISSION_PREFIX = "PERM_";

    /**
     * Resolve permissions. Returns all permissions that belongs to given roles
     *
     * @param roleAuthorities the role authorities
     * @return the granted authority[]
     */
    Collection<GrantedAuthority> resolvePermissions(Collection<? extends GrantedAuthority> roleAuthorities);

}
