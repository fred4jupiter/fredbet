package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonObjectConverterUT {

    @Test
    public void fromObjectToJsonAndBack() {
        AppUser appUser = AppUserBuilder.create().withDemoData().build();
        assertThat(appUser).isNotNull();

        JsonObjectConverter converter = new JsonObjectConverter();
        String json = converter.toJson(appUser);
        assertThat(json).isNotNull();

        AppUser convertedBack = converter.fromJson(json, AppUser.class);
        assertThat(convertedBack).isNotNull();
        assertThat(appUser.getUsername()).isEqualTo(convertedBack.getUsername());
        assertThat(appUser.getPassword()).isEqualTo(convertedBack.getPassword());
        assertThat(appUser.getRoles()).isEqualTo(convertedBack.getRoles());
        assertThat(appUser.getAppUserSetting().getTheme()).isEqualTo(convertedBack.getAppUserSetting().getTheme());
        assertThat(appUser.getAppUserSetting().getBootswatchTheme()).isEqualTo(convertedBack.getAppUserSetting().getBootswatchTheme());
        assertThat(appUser.getAppUserSetting().getNavbarLayout()).isEqualTo(convertedBack.getAppUserSetting().getNavbarLayout());
    }
}
