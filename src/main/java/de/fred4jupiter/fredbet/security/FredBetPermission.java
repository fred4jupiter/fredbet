package de.fred4jupiter.fredbet.security;

/**
 * All available permissions in FredBet application.
 * 
 * @author michael
 *
 */
public interface FredBetPermission {

	// match
	String PERM_CREATE_MATCH = "PERM_CREATE_MATCH";
	String PERM_EDIT_MATCH = "PERM_EDIT_MATCH";
	String PERM_EDIT_MATCH_RESULT = "PERM_EDIT_MATCH_RESULT";
	String PERM_DELETE_MATCH = "PERM_DELETE_MATCH";

	// users
	String PERM_CREATE_USER = "PERM_CREATE_USER";
	String PERM_EDIT_USER = "PERM_EDIT_USER";
	String PERM_PASSWORD_RESET = "PERM_PASSWORD_RESET";
	String PERM_DELETE_USER = "PERM_DELETE_USER";
	String PERM_USER_ADMINISTRATION = "PERM_USER_ADMINISTRATION";
	String PERM_CHANGE_USER_ROLE = "PERM_CHANGE_USER_ROLE";

	String PERM_ADMINISTRATION = "PERM_ADMINISTRATION";
	
	String PERM_EDIT_INFOS_RULES = "PERM_EDIT_INFOS_RULES";
	String PERM_EDIT_INFOS_PRICES = "PERM_EDIT_INFOS_PRICES";	
	
	String PERM_SHOW_ACTIVE_USERS = "PERM_SHOW_ACTIVE_USERS";
	String PERM_SHOW_LAST_LOGINS = "PERM_SHOW_LAST_LOGINS";
	
	// image group
	String PERM_EDIT_IMAGE_GROUP = "PERM_EDIT_IMAGE_GROUP";
	String PERM_DOWNLOAD_IMAGES = "PERM_DOWNLOAD_IMAGES";
	
	String PERM_DELETE_ALL_IMAGES = "PERM_DELETE_ALL_IMAGES";
}
