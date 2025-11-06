package de.fred4jupiter.fredbet.domain;

public record UserSetting(boolean darkMode) {

    public String toTheme() {
        return darkMode ? "dark" : "white";
    }
}
