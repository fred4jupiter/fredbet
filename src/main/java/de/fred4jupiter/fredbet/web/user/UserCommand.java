package de.fred4jupiter.fredbet.web.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.FredBetRole;

public class UserCommand {

	private String userId;

	private String username;

	private String password;

	private List<String> roles = new ArrayList<>();

	private final List<String> availableRoles = new ArrayList<>();

	public UserCommand() {
	    List<FredBetRole> fredBetRoles = Arrays.asList(FredBetRole.values());
	    this.availableRoles.addAll(fredBetRoles.stream().map(role -> role.name()).collect(Collectors.toList()));
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
