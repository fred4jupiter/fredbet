package de.fred4jupiter.fredbet.web.user;

public class UserDto {

	private final Long userId;
	private final String username;
	private Long userProfileImageId;

	public UserDto(Long userId, String username) {
		this.userId = userId;
		this.username = username;
	}

	public UserDto(Long userId, String username, Long userProfileImageId) {
		this(userId, username);
		this.userProfileImageId = userProfileImageId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public Long getUserProfileImageId() {
		return userProfileImageId;
	}

}
