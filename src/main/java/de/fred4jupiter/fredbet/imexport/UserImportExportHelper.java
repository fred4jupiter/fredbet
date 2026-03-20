package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.image.BinaryImage;
import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.image.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
class UserImportExportHelper {

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final ImageAdministrationService imageAdministrationService;

    private final UserService userService;

    public UserImportExportHelper(ImageMetaDataRepository imageMetaDataRepository, ImageAdministrationService imageAdministrationService,
                                  UserService userService) {
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.imageAdministrationService = imageAdministrationService;
        this.userService = userService;
    }

    public UserToExport mapToUserToExport(AppUser appUser) {
        String userProfileImage = loadUserProfileImage(appUser.getUsername());
        return new UserToExport(appUser.getUsername(), appUser.getPassword(), appUser.getRoles(), appUser.isChild(), userProfileImage);
    }

    private String loadUserProfileImage(String username) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findImageMetaDataOfUserProfileImage(username);
        if (imageMetaData == null) {
            return "";
        }

        BinaryImage binaryImage = imageAdministrationService.loadImageByImageKey(imageMetaData.getImageKey());
        byte[] encoded = Base64.getEncoder().encode(binaryImage.imageBinary());
        return new String(encoded);
    }

    public long importUsers(List<UserToExport> users) {
        users.forEach(userToExport -> {
            AppUser userToImport = AppUserBuilder.create().withUsernameAndPassword(userToExport.username(), userToExport.password())
                .withRoles(userToExport.roles()).withIsChild(userToExport.child()).build();
            userService.createUserIfNotExists(userToImport, false);

            byte[] decoded = Base64.getDecoder().decode(userToExport.userAvatarBase64());
            imageAdministrationService.saveUserProfileImage(decoded, userToImport);
        });

        return users.stream().map(UserToExport::username).distinct().count();
    }
}
