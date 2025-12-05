package de.fred4jupiter.fredbet.domain.entity;

import de.fred4jupiter.fredbet.domain.BootswatchTheme;
import de.fred4jupiter.fredbet.domain.NavbarLayout;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;

@Embeddable
public class AppUserSetting implements Serializable {

    public static final String DEFAULT_THEME = "white";

    public static final BootswatchTheme DEFAULT_BOOTSWATCH_THEME = BootswatchTheme.DEFAULT;

    public static final NavbarLayout DEFAULT_NAVBAR_LAYOUT = NavbarLayout.DARK;

    private String theme = DEFAULT_THEME;

    @Enumerated(EnumType.STRING)
    private BootswatchTheme bootswatchTheme = DEFAULT_BOOTSWATCH_THEME;

    @Enumerated(EnumType.STRING)
    private NavbarLayout navbarLayout = DEFAULT_NAVBAR_LAYOUT;

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

    public BootswatchTheme getBootswatchTheme() {
        return bootswatchTheme;
    }

    public void setBootswatchTheme(BootswatchTheme bootswatchTheme) {
        this.bootswatchTheme = bootswatchTheme;
    }

    public NavbarLayout getNavbarLayout() {
        return navbarLayout;
    }

    public void setNavbarLayout(NavbarLayout navbarLayout) {
        this.navbarLayout = navbarLayout;
    }
}
