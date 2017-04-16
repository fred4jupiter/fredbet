package de.fred4jupiter.fredbet.web.user;

public class UserDto {

	private final Long userId;
	private final String username;
	private String userProfileImageKey;

	public UserDto(Long userId, String username) {
		this.userId = userId;
		this.username = username;
	}

	public UserDto(Long userId, String username, String userProfileImageKey) {
		this(userId, username);
		this.userProfileImageKey = userProfileImageKey;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getUserProfileImageKey() {
		return userProfileImageKey;
	}

}
