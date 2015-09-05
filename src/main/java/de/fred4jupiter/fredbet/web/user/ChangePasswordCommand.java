package de.fred4jupiter.fredbet.web.user;

import de.fred4jupiter.fredbet.security.SecurityUtils;

public class ChangePasswordCommand {

    private String oldPassword;

    private String newPassword;

    private String newPasswordRepeat;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeat() {
        return newPasswordRepeat;
    }

    public void setNewPasswordRepeat(String newPasswordRepeat) {
        this.newPasswordRepeat = newPasswordRepeat;
    }

    public boolean isPasswordRepeatMismatch() {
        return !this.newPassword.equals(this.newPasswordRepeat);
    }
}
