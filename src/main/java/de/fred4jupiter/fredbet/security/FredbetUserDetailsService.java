package de.fred4jupiter.fredbet.security;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class FredbetUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    private final PermissionResolver permissionResolver;

    public FredbetUserDetailsService(AppUserRepository appUserRepository, PermissionResolver permissionResolver) {
        this.appUserRepository = appUserRepository;
        this.permissionResolver = permissionResolver;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AppUser appUser = this.appUserRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("Could not found user with username=" + username);
        }
        Collection<GrantedAuthority> permissions = permissionResolver.resolvePermissions(appUser.getAuthorities());
        appUser.setRoles(toRoles(permissions));
        return appUser;
    }

    private Set<String> toRoles(Collection<GrantedAuthority> permissions) {
        return permissions.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

}
