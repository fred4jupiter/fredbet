package de.fred4jupiter.fredbet.domain.entity;

import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AppUserSetting that = (AppUserSetting) o;

        return new EqualsBuilder().append(theme, that.theme).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(theme).toHashCode();
    }
}
