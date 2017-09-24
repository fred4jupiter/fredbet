package de.fred4jupiter.fredbet.web.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.ui.ModelMap;

import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

public class EditUserCommand {

	private Long userId;

	@NotEmpty
	@Size(min = 2, max = 12)
	private String username;

	private boolean deletable;

	private boolean resetPassword;

	@NotEmpty
	private List<String> roles = new ArrayList<>();

	private final List<String> availableRoles;

	public EditUserCommand() {
		List<FredBetRole> fredBetRoles = Arrays.asList(FredBetRole.values());
		this.availableRoles = Collections.unmodifiableList(fredBetRoles.stream().map(role -> role.name()).collect(Collectors.toList()));
	}

	public List<String> getAvailableRoles() {
		return availableRoles;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void addRole(String role) {
		roles.add(role);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean validate(WebMessageUtil messageUtil, ModelMap modelMap) {
		if (StringUtils.isEmpty(this.username)) {
			messageUtil.addErrorMsg(modelMap, "user.validation.emptyUsername");
			return true;
		}
		if (this.username.contains(" ")) {
			messageUtil.addErrorMsg(modelMap, "user.validation.username.containsBlanks");
			return true;
		}
		final int fieldLength = 12;
		if (this.username.length() > fieldLength) {
			messageUtil.addErrorMsg(modelMap, "user.validation.username.tooLong", fieldLength);
			return true;
		}
		
		if (Validator.isEmpty(this.roles)) {
			messageUtil.addErrorMsg(modelMap, "user.validation.roles");
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("userId", userId);
		builder.append("roles", roles);
		return builder.toString();
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public boolean isResetPassword() {
		return resetPassword;
	}

	public void setResetPassword(boolean resetPassword) {
		this.resetPassword = resetPassword;
	}

}
