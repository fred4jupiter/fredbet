package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.avatar.AvatarService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.image.group.ImageGroupRepository;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class ImageAdministrationServiceUT {

    @Mock
    private ImageMetaDataRepository imageMetaDataRepository;

    @Mock
    private ImageBinaryRepository imageBinaryRepository;

    @Mock
    private ImageGroupRepository imageGroupRepository;

    @Mock
    private ImageResizingService imageResizingService;

    @Mock
    private RuntimeSettingsService runtimeSettingsService;

    @Mock
    private AvatarService avatarService;

    @Test
    public void initUserProfileImageGroupCreatesGroupWhenMissing() {
        when(imageGroupRepository.findByUserProfileImageGroup()).thenReturn(null);
        when(imageGroupRepository.save(any(ImageGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImageGroup imageGroup = createService().initUserProfileImageGroup();

        assertThat(imageGroup.getName()).isEqualTo(FredbetConstants.GALLERY_NAME);
        assertThat(imageGroup.isUserProfileImageGroup()).isTrue();
    }

    @Test
    public void findOrCreateImageGroupUsesDefaultNameForBlankGroup() {
        when(imageGroupRepository.findByName(" ")).thenReturn(null);
        when(imageGroupRepository.save(any(ImageGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImageGroup imageGroup = createService().findOrCreateImageGroup(" ");

        assertThat(imageGroup.getName()).isEqualTo(FredbetConstants.DEFAULT_IMAGE_GROUP_NAME);
    }

    @Test
    public void saveImageFailsWhenUploadLimitIsReached() {
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setImageUploadLimit(1);
        AppUser user = new AppUser();
        user.setUsername("alfredo");

        when(imageResizingService.createThumbnail(new byte[]{1})).thenReturn(new byte[]{2});
        when(imageBinaryRepository.saveImage(new byte[]{1}, new byte[]{2})).thenReturn("img-key");
        when(imageGroupRepository.findByName("gallery")).thenReturn(new ImageGroup("gallery"));
        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(runtimeSettings);
        when(imageMetaDataRepository.numberOfImagesOfUser("alfredo")).thenReturn(1);

        assertThatThrownBy(() -> createService().saveImage(new byte[]{1}, "gallery", "desc", user))
            .isInstanceOf(ImageUploadLimitReachedException.class)
            .extracting("limit")
            .isEqualTo(1);

        verify(imageMetaDataRepository, never()).save(any());
    }

    @Test
    public void deleteImageByImageKeyReturnsWhenImageDoesNotExist() {
        when(imageMetaDataRepository.findByImageKey("missing")).thenReturn(null);

        createService().deleteImageByImageKey("missing");

        verify(imageMetaDataRepository, never()).delete(any());
        verify(imageBinaryRepository, never()).deleteImage(any());
    }

    private ImageAdministrationService createService() {
        return new ImageAdministrationService(imageMetaDataRepository, imageBinaryRepository, imageGroupRepository,
            imageResizingService, runtimeSettingsService, avatarService);
    }
}

