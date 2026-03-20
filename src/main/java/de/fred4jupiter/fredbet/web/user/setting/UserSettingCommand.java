package de.fred4jupiter.fredbet.web.user.setting;

import de.fred4jupiter.fredbet.domain.BootswatchTheme;
import de.fred4jupiter.fredbet.domain.NavbarLayout;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserSettingCommand {

    private boolean darkMode;

    @NotNull
    private BootswatchTheme bootswatchTheme = BootswatchTheme.DEFAULT;

    @NotNull
    private NavbarLayout navbarLayout = NavbarLayout.DARK;

    public BootswatchTheme getBootswatchTheme() {
        return bootswatchTheme;
    }

    public void setBootswatchTheme(BootswatchTheme bootswatchTheme) {
        this.bootswatchTheme = bootswatchTheme;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public NavbarLayout getNavbarLayout() {
        return navbarLayout;
    }

    public void setNavbarLayout(NavbarLayout navbarLayout) {
        this.navbarLayout = navbarLayout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("darkMode", darkMode)
            .append("bootswatchTheme", bootswatchTheme)
            .append("navbarLayout", navbarLayout)
            .toString();
    }
}
