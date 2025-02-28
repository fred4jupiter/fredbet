package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.service.image.BinaryImage;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
class UserImportExportHelper {

    private static final Logger LOG = LoggerFactory.getLogger(UserImportExportHelper.class);

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final ImageAdministrationService imageAdministrationService;

    private final UserService userService;

    public UserImportExportHelper(ImageMetaDataRepository imageMetaDataRepository, ImageAdministrationService imageAdministrationService, UserService userService) {
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.imageAdministrationService = imageAdministrationService;
        this.userService = userService;
    }

    public UserToExport mapToUserToExport(AppUser appUser) {
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

    public long importUsers(List<UserToExport> users) {
        users.forEach(userToExport -> {
            AppUser userToImport = AppUserBuilder.create().withUsernameAndPassword(userToExport.getUsername(), userToExport.getPassword())
                .withRoles(userToExport.getRoles()).withIsChild(userToExport.isChild()).build();
            userService.createUserIfNotExists(userToImport);
        });

        return users.stream().map(UserToExport::getUsername).distinct().count();
    }
}
