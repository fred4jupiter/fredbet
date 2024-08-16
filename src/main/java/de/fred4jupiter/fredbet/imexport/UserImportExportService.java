package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserImportExportService {

    private final AppUserRepository appUserRepository;

    private final JsonObjectConverter jsonObjectConverter;

    private final UserImportExportHelper userImportExportHelper;

    public UserImportExportService(AppUserRepository appUserRepository, JsonObjectConverter jsonObjectConverter,
                                   UserImportExportHelper userImportExportHelper) {
        this.appUserRepository = appUserRepository;
        this.jsonObjectConverter = jsonObjectConverter;
        this.userImportExportHelper = userImportExportHelper;
    }

    public String exportAllUsersToJson() {
        List<AppUser> allUsers = appUserRepository.findAll();

        List<UserToExport> usersToExport = allUsers.stream().filter(AppUser::isDeletable).map(userImportExportHelper::mapToUserToExport).toList();
        UserContainer userContainer = new UserContainer(usersToExport);

        return jsonObjectConverter.toJson(userContainer);
    }

    public long importUsers(String json) {
        UserContainer userContainer = jsonObjectConverter.fromJson(json, UserContainer.class);
        return userImportExportHelper.importUsers(userContainer.userList());
    }
}
