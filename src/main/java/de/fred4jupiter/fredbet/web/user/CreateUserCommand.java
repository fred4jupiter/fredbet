package de.fred4jupiter.fredbet.web.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import de.fred4jupiter.fredbet.security.FredBetRole;

public class CreateUserCommand {

	private Long userId;

	@NotEmpty
	@Size(min = 2, max = 12)
	private String username;

	@NotEmpty
	private String password;

	@NotEmpty
	private List<String> roles = new ArrayList<>();

	private final List<String> availableRoles;

	public CreateUserCommand() {
		List<FredBetRole> fredBetRoles = Arrays.asList(FredBetRole.values());
		this.availableRoles = Collections.unmodifiableList(fredBetRoles.stream().map(role -> role.name()).collect(Collectors.toList()));
		this.roles.add(FredBetRole.ROLE_USER.name());
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
