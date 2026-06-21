package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.BootswatchTheme;
import de.fred4jupiter.fredbet.domain.NavbarLayout;
import de.fred4jupiter.fredbet.domain.UserSetting;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.AppUserSetting;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class UserSettingsServiceUT {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private UserSettingsService userSettingsService;

    @Test
    public void updateUserSetting_whenUserExists_updatesBothAndSaves() throws Exception {
        AppUser currentUser = createAppUser(11L, "current");
        AppUser storedUser = createAppUser(11L, "stored");

        when(appUserRepository.findById(11L)).thenReturn(Optional.of(storedUser));

        UserSetting userSetting = new UserSetting(true, BootswatchTheme.CYBORG, NavbarLayout.LIGHT);

        userSettingsService.updateUserSetting(currentUser, userSetting);

        assertThat(currentUser.getAppUserSetting().getTheme()).isEqualTo("dark");
        assertThat(currentUser.getAppUserSetting().getBootswatchTheme()).isEqualTo(BootswatchTheme.CYBORG);
        assertThat(currentUser.getAppUserSetting().getNavbarLayout()).isEqualTo(NavbarLayout.LIGHT);

        assertThat(storedUser.getAppUserSetting().getTheme()).isEqualTo("dark");
        assertThat(storedUser.getAppUserSetting().getBootswatchTheme()).isEqualTo(BootswatchTheme.CYBORG);
        assertThat(storedUser.getAppUserSetting().getNavbarLayout()).isEqualTo(NavbarLayout.LIGHT);

        verify(appUserRepository).save(storedUser);
    }

    @Test
    public void updateUserSetting_whenUserMissing_updatesCurrentOnly() throws Exception {
        AppUser currentUser = createAppUser(99L, "current");
        when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

        UserSetting userSetting = new UserSetting(false, BootswatchTheme.VAPOR, NavbarLayout.DARK);

        userSettingsService.updateUserSetting(currentUser, userSetting);

        assertThat(currentUser.getAppUserSetting().getTheme()).isEqualTo("white");
        assertThat(currentUser.getAppUserSetting().getBootswatchTheme()).isEqualTo(BootswatchTheme.VAPOR);
        assertThat(currentUser.getAppUserSetting().getNavbarLayout()).isEqualTo(NavbarLayout.DARK);
        verify(appUserRepository, never()).save(org.mockito.ArgumentMatchers.any(AppUser.class));
    }

    private AppUser createAppUser(Long id, String username) throws Exception {
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setAppUserSetting(new AppUserSetting());

        Field idField = AppUser.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(appUser, id);

        return appUser;
    }
}

