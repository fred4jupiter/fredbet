package de.fred4jupiter.fredbet.service.user;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UserImportExportService {

    private static final Logger LOG = LoggerFactory.getLogger(UserImportExportService.class);

    private final AppUserRepository appUserRepository;

    private final JsonObjectConverter jsonObjectConverter;

    public UserImportExportService(AppUserRepository appUserRepository, JsonObjectConverter jsonObjectConverter) {
        this.appUserRepository = appUserRepository;
        this.jsonObjectConverter = jsonObjectConverter;
    }

    public String exportAllUsersToJson() {
        List<AppUser> allUsers = appUserRepository.findAll();

        UserContainer userContainer = new UserContainer();
        List<UserToExport> usersToExport = allUsers.stream().filter(AppUser::isDeletable).map(this::mapToUserToExport).collect(Collectors.toList());
        userContainer.setUserList(usersToExport);

        return jsonObjectConverter.toJson(userContainer);
    }

    public int importUsers(String json) {
        final AtomicInteger counter = new AtomicInteger();
        UserContainer userContainer = jsonObjectConverter.fromJson(json, UserContainer.class);
        userContainer.getUserList().forEach(userToExport -> {
            createUserIfNotExists(userToExport, counter);
        });
        return counter.get();
    }

    private void createUserIfNotExists(UserToExport userToExport, AtomicInteger counter) {
        AppUser appUser = appUserRepository.findByUsername(userToExport.getUsername());
        if (appUser != null) {
            LOG.warn("user with username={} already exists.", userToExport.getUsername());
            return;
        }
        AppUser newAppUser = AppUserBuilder.create().withUsernameAndPassword(userToExport.getUsername(), userToExport.getPassword())
                .withRoles(userToExport.getRoles()).withIsChild(userToExport.isChild()).build();
        appUserRepository.save(newAppUser);
        counter.incrementAndGet();
    }

    private UserToExport mapToUserToExport(AppUser appUser) {
        UserToExport userToExport = new UserToExport();
        userToExport.setUsername(appUser.getUsername());
        userToExport.setPassword(appUser.getPassword());
        userToExport.setChild(appUser.isChild());
        userToExport.setRoles(appUser.getRoles());
        return userToExport;
    }
}
