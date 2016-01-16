package de.fred4jupiter.fredbet.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * Aufloesung von Rollen in ein Array von Rechten
 * 
 * @author mis
 *
 */
public interface PermissionResolver {

    public static final String PERMISSION_PREFIX = "PERM_";
    
    /**
     * Resolve permissions. Returns all permissions that belongs to given roles
     * 
     * @param roleAuthorities
     *            the role authorities
     * 
     * @return the granted authority[]
     */
    Collection<GrantedAuthority> resolvePermissions(Collection<? extends GrantedAuthority> roleAuthorities);

}
