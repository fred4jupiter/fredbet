package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
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
        assertThat(appUser).isEqualTo(convertedBack);
    }
}
