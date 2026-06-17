package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.image.BinaryImage;
import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.image.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class UserImportExportHelperUT {

    @InjectMocks
    private UserImportExportHelper userImportExportHelper;

    @Mock
    private ImageMetaDataRepository imageMetaDataRepository;

    @Mock
    private ImageAdministrationService imageAdministrationService;

    @Mock
    private UserService userService;

    @Test
    public void mapToUserToExportIncludesBase64EncodedProfileImage() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("alfredo", "secret").withRoles(Set.of("ROLE_USER")).build();
        ImageMetaData imageMetaData = new ImageMetaData("img-1", new ImageGroup("profile", true), appUser);
        when(imageMetaDataRepository.findImageMetaDataOfUserProfileImage("alfredo")).thenReturn(imageMetaData);
        when(imageAdministrationService.loadImageByImageKey("img-1")).thenReturn(new BinaryImage("img-1", new byte[]{1, 2, 3}));

        UserToExport userToExport = userImportExportHelper.mapToUserToExport(appUser);

        assertThat(userToExport.userAvatarBase64()).isEqualTo(Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}));
    }

    @Test
    public void importUsersCreatesDistinctUsersAndStoresProfileImages() {
        UserToExport one = new UserToExport("alfredo", "pw", Set.of("ROLE_USER"), false, Base64.getEncoder().encodeToString(new byte[]{9}));
        UserToExport duplicate = new UserToExport("alfredo", "pw2", Set.of("ROLE_USER"), true, Base64.getEncoder().encodeToString(new byte[]{8}));

        long count = userImportExportHelper.importUsers(List.of(one, duplicate));

        assertThat(count).isEqualTo(1);
        verify(userService, times(2)).createUserIfNotExists(any(AppUser.class), org.mockito.ArgumentMatchers.eq(false));
        verify(imageAdministrationService, times(2)).saveUserProfileImage(any(byte[].class), any(AppUser.class));
    }
}

