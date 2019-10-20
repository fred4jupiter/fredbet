package de.fred4jupiter.fredbet.web.registration;

import de.fred4jupiter.fredbet.web.validation.PasswordRepeatConstraint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@PasswordRepeatConstraint(message = "{msg.bet.betting.error.passwordMismatch}")
public class RegistrationCommand {

    @NotEmpty
    private String token;

    @NotEmpty
    @Size(min = 2, max = 12)
    private String username;

    @NotEmpty
    @Size(min = 4, max = 12)
    private String newPassword;

    @NotEmpty
    @Size(min = 4, max = 12)
    private String newPasswordRepeat;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
