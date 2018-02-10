package de.fred4jupiter.fredbet.web.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import de.fred4jupiter.fredbet.security.FredBetRole;

public class EditUserCommand {

	private Long userId;

	@NotEmpty
	@Size(min = 2, max = 12)
	private String username;

	private boolean deletable;

	private boolean resetPassword;

	@NotEmpty
	private Set<String> roles = new HashSet<>();

	private final List<String> availableRoles;
	
	private boolean child;

	public EditUserCommand() {
		List<FredBetRole> fredBetRoles = Arrays.asList(FredBetRole.values());
		this.availableRoles = Collections.unmodifiableList(fredBetRoles.stream().map(role -> role.name()).collect(Collectors.toList()));		
	}

	public List<String> getAvailableRoles() {
		return availableRoles;
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

	public boolean isChild() {
		return child;
	}

	public void setChild(boolean child) {
		this.child = child;
	}

}
