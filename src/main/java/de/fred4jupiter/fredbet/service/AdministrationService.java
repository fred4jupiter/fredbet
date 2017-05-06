package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;

@Service
public class AdministrationService {

	@Autowired
	private AppUserRepository appUserRepository;
	
	public List<AppUser> fetchLastLoginUsers() {
		return appUserRepository.findByLastLoginNotNull();
	}

}
