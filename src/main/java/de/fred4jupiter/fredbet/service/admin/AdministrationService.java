package de.fred4jupiter.fredbet.service.admin;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrationService {

    private final AppUserRepository appUserRepository;

    public AdministrationService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public List<AppUser> fetchLastLoginUsers() {
        return appUserRepository.fetchLastLoginUsers();
    }

}
