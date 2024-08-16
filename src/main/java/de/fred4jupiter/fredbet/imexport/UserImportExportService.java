package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.service.image.BinaryImage;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class UserImportExportService {

    private final AppUserRepository appUserRepository;

    private final JsonObjectConverter jsonObjectConverter;

    private final UserService userService;

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final ImageAdministrationService imageAdministrationService;

    public UserImportExportService(AppUserRepository appUserRepository, JsonObjectConverter jsonObjectConverter,
                                   UserService userService, ImageMetaDataRepository imageMetaDataRepository,
                                   ImageAdministrationService imageAdministrationService) {
        this.appUserRepository = appUserRepository;
        this.jsonObjectConverter = jsonObjectConverter;
        this.userService = userService;
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.imageAdministrationService = imageAdministrationService;
    }

    public String exportAllUsersToJson() {
        List<AppUser> allUsers = appUserRepository.findAll();

        UserContainer userContainer = new UserContainer();
        List<UserToExport> usersToExport = allUsers.stream().filter(AppUser::isDeletable).map(this::mapToUserToExport).toList();
        userContainer.setUserList(usersToExport);

        return jsonObjectConverter.toJson(userContainer);
    }

    public long importUsers(String json) {
        UserContainer userContainer = jsonObjectConverter.fromJson(json, UserContainer.class);
        userContainer.getUserList().forEach(userToExport -> {
            AppUser appUser = userService.createUserIfNotExists(userToExport.getUsername(), userToExport.getPassword(), userToExport.isChild(), userToExport.getRoles());
            if (StringUtils.isNotBlank(userToExport.getUserAvatarBase64())) {
                byte[] decoded = Base64.getDecoder().decode(userToExport.getUserAvatarBase64());
                imageAdministrationService.saveUserProfileImage(decoded, appUser);
            }
        });

        return userContainer.getUserList().stream().map(UserToExport::getUsername).distinct().count();
    }

    private UserToExport mapToUserToExport(AppUser appUser) {
        UserToExport userToExport = new UserToExport();
        userToExport.setUsername(appUser.getUsername());
        userToExport.setPassword(appUser.getPassword());
        userToExport.setChild(appUser.isChild());
        userToExport.setRoles(appUser.getRoles());

        ImageMetaData imageMetaData = imageMetaDataRepository.findImageMetaDataOfUserProfileImage(appUser.getUsername());
        if (imageMetaData != null) {
            BinaryImage binaryImage = imageAdministrationService.loadImageByImageKey(imageMetaData.getImageKey());
            byte[] encoded = Base64.getEncoder().encode(binaryImage.imageBinary());
            userToExport.setUserAvatarBase64(new String(encoded));
            userToExport.setImageGroupName(imageMetaData.getImageGroup().getName());
        }
        return userToExport;
    }
}
