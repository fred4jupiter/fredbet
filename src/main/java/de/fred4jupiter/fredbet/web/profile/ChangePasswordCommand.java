package de.fred4jupiter.fredbet.web.profile;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;

import de.fred4jupiter.fredbet.web.WebMessageUtil;

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

	public boolean validate(WebMessageUtil messageUtil, ModelMap modelMap) {
		if (StringUtils.isBlank(this.oldPassword)) {
			messageUtil.addErrorMsg(modelMap, "user.changePassword.oldPassword");
			return true;
		}

		if (StringUtils.isBlank(this.newPassword)) {
			messageUtil.addErrorMsg(modelMap, "user.changePassword.newPassword");
			return true;
		}
		if (StringUtils.isBlank(this.newPasswordRepeat)) {
			messageUtil.addErrorMsg(modelMap, "user.changePassword.newPasswordRepeat");
			return true;
		}
		return false;
	}
}
