package de.fred4jupiter.fredbet.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

@Document
public class AppUser implements UserDetails {

	private static final long serialVersionUID = -2973861561213230375L;

	@Id
	private String id;

	private List<String> roles;

	@Indexed(unique = true)
	private String username;

	private String password;

	public AppUser(String username, String password, String... roles) {
		this(username, password, Arrays.asList(roles));
	}

	@PersistenceConstructor
	public AppUser(String username, String password, List<String> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	public String getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (CollectionUtils.isEmpty(roles)) {
			return null;
		}
		return roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getRolesAsString() {
		if (CollectionUtils.isEmpty(roles)) {
			return null;
		}
		return roles.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
