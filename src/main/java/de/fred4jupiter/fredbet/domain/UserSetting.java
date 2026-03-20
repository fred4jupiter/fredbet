package de.fred4jupiter.fredbet.domain;

public record UserSetting(boolean darkMode, BootswatchTheme bootswatchTheme, NavbarLayout navbarLayout) {

    public String toTheme() {
        return darkMode ? "dark" : "white";
    }
}
