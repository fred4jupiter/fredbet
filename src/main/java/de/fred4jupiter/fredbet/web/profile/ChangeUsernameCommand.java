package de.fred4jupiter.fredbet.web.profile;

import de.fred4jupiter.fredbet.props.FredbetConstants;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ChangeUsernameCommand {

	@NotEmpty
	@Size(min = 2, max = FredbetConstants.USERNAME_MAX_LENGTH)
	private String newUsername;

	public String getNewUsername() {
		return newUsername;
	}

	public void setNewUsername(String newUsername) {
		this.newUsername = newUsername;
	}

}
