package de.fred4jupiter.fredbet.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;

/**
 * Provides security informations of the current user.
 * 
 * @author mstaehler
 *
 */
@Service
public class SecurityService {

	@Autowired
	private ImageMetaDataRepository imageMetaDataRepository;

	@Autowired
	private AppUserRepository appUserRepository;
	
	public boolean isUserLoggedIn() {
		try {
			getCurrentUser();
			return true;
		} catch (UsernameNotFoundException e) {
			return false;
		}
	}

	public String getCurrentUserName() {
		return getCurrentUser().getUsername();
	}

	public boolean isCurrentUserHavingPermission(String permission) {
		return getCurrentUser().hasPermission(permission);
	}

	public AppUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof AppUser)) {
			throw new UsernameNotFoundException("User is not logged in!");
		}

		return (AppUser) authentication.getPrincipal();
	}

	public ImageMetaData getCurrentUserProfileImageMetaData() {
		return getProfileImageMetaDataFor(getCurrentUserName());
	}

	public ImageMetaData getProfileImageMetaDataFor(String username) {
		return imageMetaDataRepository.findImageMetaDataOfUserProfileImage(username);
	}

	/**
	 * This is the user which is created by default and cannot be renamed.
	 * 
	 * @return
	 */
	public boolean isTechnicalUser(Long userId) {
		Optional<AppUser> appUserOpt = appUserRepository.findById(userId);
		if (!appUserOpt.isPresent()) {
			return true;
		}
		
		AppUser appUser = appUserOpt.get();
		return appUser.isTechnicalDefaultUser();		
	}
}
