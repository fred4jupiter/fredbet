package de.fred4jupiter.fredbet.web.user;

import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class CreateUserCommand {

	private Long userId;

	@NotEmpty
	@Size(min = 2, max = 12)
	private String username;

	@NotEmpty
	@Size(min = 4, max = 12)
	private String password;

	@NotEmpty
	private Set<String> roles = new HashSet<>();

	private boolean child;

	public CreateUserCommand() {
		this.roles.add(FredBetUserGroup.ROLE_USER.name());
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
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

	public boolean isChild() {
		return child;
	}

	public void setChild(boolean child) {
		this.child = child;
	}

}
