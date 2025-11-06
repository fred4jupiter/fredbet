package de.fred4jupiter.fredbet.web.user.setting;

public class UserSettingCommand {

    private boolean darkMode;

    public UserSettingCommand(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
}
