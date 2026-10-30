package de.fred4jupiter.fredbet.security;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.user.AppUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // the roles are left untouched, otherwise the permissions would be saved as roles if the user entity is still managed
        List<GrantedAuthority> roles = appUser.getRoles() != null ? appUser.getRoles().stream().<GrantedAuthority>map(SimpleGrantedAuthority::new).toList() : List.of();
        appUser.setPermissions(permissionResolver.resolvePermissions(roles));
        return appUser;
    }

}
