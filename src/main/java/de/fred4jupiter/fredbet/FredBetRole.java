package de.fred4jupiter.fredbet;

public enum FredBetRole {

	/**
	 * Normal user role with permission to bet matches.
	 */
	ROLE_USER,
	
	/**
	 * Like the user role but with permission to enter the results of a match.
	 */
	ROLE_EDIT_MATCH,

	/**
	 * All permissions.
	 */
	ROLE_ADMIN;

}
