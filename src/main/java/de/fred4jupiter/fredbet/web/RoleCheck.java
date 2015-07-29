package de.fred4jupiter.fredbet.web;

public class RoleCheck {

	private String role;

	private Boolean enabled;

	public RoleCheck(String role, Boolean enabled) {
		super();
		this.role = role;
		this.enabled = enabled;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
