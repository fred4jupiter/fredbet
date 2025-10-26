package de.fred4jupiter.fredbet.domain.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AppUserSetting implements Serializable {

    public static final String DEFAULT_THEME = "white";

    private String theme;

    public AppUserSetting() {
        this.theme = DEFAULT_THEME;
    }

    public AppUserSetting(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        if (this.theme == null) {
            return new AppUserSetting().getTheme();
        }
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isDarkMode() {
        return "dark".equals(getTheme());
    }
}
