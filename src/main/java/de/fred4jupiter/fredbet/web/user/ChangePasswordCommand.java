package de.fred4jupiter.fredbet.web.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;

import de.fred4jupiter.fredbet.web.MessageUtil;

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

	public boolean validate(MessageUtil messageUtil, ModelMap modelMap) {
		if (StringUtils.isBlank(this.oldPassword)) {
			messageUtil.addPlainErrorMsg(modelMap, "Bitte geben Sie das alte Passwort ein!");
			return true;
		}

		if (StringUtils.isBlank(this.newPassword)) {
			messageUtil.addPlainErrorMsg(modelMap, "Bitte geben Sie das neue Passwort ein!");
			return true;
		}
		if (StringUtils.isBlank(this.newPasswordRepeat)) {
			messageUtil.addPlainErrorMsg(modelMap, "Bitte geben Sie die Passwortwiederholung ein!");
			return true;
		}
		return false;
	}
}
