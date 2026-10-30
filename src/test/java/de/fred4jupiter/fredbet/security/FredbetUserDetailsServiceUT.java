package de.fred4jupiter.fredbet.security;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.user.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@UnitTest
public class FredbetUserDetailsServiceUT {

    @InjectMocks
    private FredbetUserDetailsService fredbetUserDetailsService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PermissionResolver permissionResolver;

    @Test
    public void loadUserByUsernameThrowsWhenUserDoesNotExist() {
        when(appUserRepository.findByUsername("missing")).thenReturn(null);

        assertThatThrownBy(() -> fredbetUserDetailsService.loadUserByUsername("missing"))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("missing");
    }

    @Test
    public void loadUserByUsernameResolvesPermissionsWithoutChangingRoles() {
        AppUser appUser = new AppUser();
        appUser.setUsername("alfredo");
        appUser.addUserGroup(FredBetUserGroup.ROLE_ADMIN);
        when(appUserRepository.findByUsername("alfredo")).thenReturn(appUser);
        when(permissionResolver.resolvePermissions(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))).thenReturn(List.of(
            new SimpleGrantedAuthority("PERM_A"), new SimpleGrantedAuthority("PERM_B")));

        AppUser loaded = (AppUser) fredbetUserDetailsService.loadUserByUsername("alfredo");

        assertThat(loaded.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactlyInAnyOrder("PERM_A", "PERM_B");
        assertThat(loaded.getRoles()).isEqualTo(Set.of("ROLE_ADMIN"));
    }
}

