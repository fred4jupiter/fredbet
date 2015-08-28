package de.fred4jupiter.fredbet.web.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserCommand {

	private String userId;

	private String username;

	private String password;

	private List<String> roles = new ArrayList<>();

	private final List<String> availableRoles = new ArrayList<>();

	public UserCommand() {
		availableRoles.add("ROLE_USER");
		availableRoles.add("ROLE_ADMIN");
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("userId", userId);
		builder.append("password", password != null ? "is set" : "is null");
		builder.append("roles", roles);
		return builder.toString();
	}
}
