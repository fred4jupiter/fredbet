package de.fred4jupiter.fredbet.web.registration;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.web.validation.PasswordRepeatConstraint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@PasswordRepeatConstraint(message = "{msg.bet.betting.error.passwordMismatch}")
public class RegistrationCommand {

    @NotEmpty
    private String token;

    @NotEmpty
    @Size(min = 2, max = FredbetConstants.USERNAME_MAX_LENGTH)
    private String username;

    @NotEmpty
    @Size(min = 4, max = FredbetConstants.PASSWORD_MAX_LENGTH)
    private String password;

    @NotEmpty
    @Size(min = 4, max = FredbetConstants.PASSWORD_MAX_LENGTH)
    private String passwordRepeat;

    private boolean child;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }
}
