package de.fred4jupiter.fredbet.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserCommand {

	private String userId;

	private String username;

	private String password;

	private List<RoleCheck> roleCheckList = new ArrayList<>();
	
	public UserCommand() {
		roleCheckList.add(new RoleCheck("ROLE_USER", false));
		roleCheckList.add(new RoleCheck("ROLE_ADMIN", false));
	}
	
	public void addRole(String role) {
		for (RoleCheck roleCheck : roleCheckList) {
			if (roleCheck.getRole().equals(role)) {
				roleCheck.setEnabled(true);
			}
		}
	}
	
	public List<String> toRoles() {
		return roleCheckList.stream().map(roleCheck -> roleCheck.getRole()).collect(Collectors.toList());
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

	public List<RoleCheck> getRoleCheckList() {
		return roleCheckList;
	}

	public void setRoleCheckList(List<RoleCheck> roleCheckList) {
		this.roleCheckList = roleCheckList;
	}

}
