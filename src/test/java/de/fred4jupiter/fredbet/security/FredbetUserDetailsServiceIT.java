package de.fred4jupiter.fredbet.security;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.user.AppUserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class FredbetUserDetailsServiceIT {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void loadingTheUserInATransactionKeepsTheStoredRoles() {
        // within one transaction both calls get the same managed entity
        AppUser first = (AppUser) userDetailsService.loadUserByUsername("admin");
        AppUser second = (AppUser) userDetailsService.loadUserByUsername("admin");

        assertThat(second).isSameAs(first);
        assertThat(second.getAuthorities()).extracting(GrantedAuthority::getAuthority).contains(FredBetPermission.PERM_ADMINISTRATION);

        entityManager.flush();
        entityManager.clear();
        assertThat(appUserRepository.findByUsername("admin").getRoles()).containsExactly(FredBetUserGroup.ROLE_ADMIN.name());
    }
}
