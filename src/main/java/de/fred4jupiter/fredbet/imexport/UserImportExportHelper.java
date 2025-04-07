package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.image.BinaryImage;
import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.image.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.image.storage.ImageLocationStrategy;
import de.fred4jupiter.fredbet.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
class UserImportExportHelper {

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final ImageAdministrationService imageAdministrationService;

    private final UserService userService;

    private final ImageLocationStrategy imageLocationStrategy;

    public UserImportExportHelper(ImageMetaDataRepository imageMetaDataRepository, ImageAdministrationService imageAdministrationService,
                                  UserService userService, ImageLocationStrategy imageLocationStrategy) {
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.imageAdministrationService = imageAdministrationService;
        this.userService = userService;
        this.imageLocationStrategy = imageLocationStrategy;
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
            userToExport.setImageKey(imageMetaData.getImageKey());

            BinaryImage thumbBinary = imageAdministrationService.loadThumbnailByImageKey(imageMetaData.getImageKey());
            byte[] encodedThumb = Base64.getEncoder().encode(thumbBinary.imageBinary());
            userToExport.setUserAvatarThumbBase64(new String(encodedThumb));
        }

        return userToExport;
    }

    public long importUsers(List<UserToExport> users) {
        users.forEach(userToExport -> {
            AppUser userToImport = AppUserBuilder.create().withUsernameAndPassword(userToExport.getUsername(), userToExport.getPassword())
                .withRoles(userToExport.getRoles()).withIsChild(userToExport.isChild()).build();
            userService.createUserIfNotExists(userToImport);

            byte[] decoded = Base64.getDecoder().decode(userToExport.getUserAvatarBase64());
            userService.saveUserProfileImage(decoded, userToImport.getUsername());
        });

        return users.stream().map(UserToExport::getUsername).distinct().count();
    }
}
