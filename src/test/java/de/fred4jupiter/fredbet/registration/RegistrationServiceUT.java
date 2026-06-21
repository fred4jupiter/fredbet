package de.fred4jupiter.fredbet.registration;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class RegistrationServiceUT {

    @Mock
    private UserService userService;

    @Mock
    private RuntimeSettingsService runtimeSettingsService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    public void isTokenValid_whenTokenIsBlank_returnsFalse() {
        assertThat(registrationService.isTokenValid(" ")).isFalse();
    }

    @Test
    public void isTokenValid_whenRegistrationCodeIsMissing_returnsFalse() {
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setRegistrationCode(" ");
        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(runtimeSettings);

        assertThat(registrationService.isTokenValid("token")).isFalse();
    }

    @Test
    public void isTokenValid_whenTokenMatchesTrimmedCode_returnsTrue() {
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setRegistrationCode("  super-secret  ");
        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(runtimeSettings);

        assertThat(registrationService.isTokenValid(" super-secret ")).isTrue();
    }

    @Test
    public void isSelfRegistrationEnabled_returnsConfiguredValue() {
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setSelfRegistrationEnabled(true);
        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(runtimeSettings);

        assertThat(registrationService.isSelfRegistrationEnabled()).isTrue();
    }

    @Test
    public void registerNewUser_createsStandardRoleUser() {
        registrationService.registerNewUser("alice", "pw", true);

        ArgumentCaptor<AppUser> appUserCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userService).createUser(appUserCaptor.capture());

        AppUser createdUser = appUserCaptor.getValue();
        assertThat(createdUser.getUsername()).isEqualTo("alice");
        assertThat(createdUser.getPassword()).isEqualTo("pw");
        assertThat(createdUser.isChild()).isTrue();
        assertThat(createdUser.getRoles()).contains(FredBetUserGroup.ROLE_USER.name());
    }
}

