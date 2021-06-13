package de.fred4jupiter.fredbet.web.profile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.web.validation.PasswordChangeConstraint;

@PasswordChangeConstraint(message = "{msg.bet.betting.error.passwordMismatch}")
public class ChangePasswordCommand {

	@NotEmpty
	private String oldPassword;

	@NotEmpty
	@Size(min = 4, max = FredbetConstants.PASSWORD_MAX_LENGTH)
	private String newPassword;

	@NotEmpty
	@Size(min = 4, max = FredbetConstants.PASSWORD_MAX_LENGTH)
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

}
