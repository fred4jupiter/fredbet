package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.image.group.ImageGroupRepository;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ImageAdministrationService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageAdministrationService.class);

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final ImageBinaryRepository imageBinaryRepository;

    private final ImageGroupRepository imageGroupRepository;

    private final ImageResizingService imageResizingService;

    private final RuntimeSettingsService runtimeSettingsService;

    private final DefaultProfileImageLoader defaultProfileImageLoader;

    ImageAdministrationService(ImageMetaDataRepository imageMetaDataRepository, ImageBinaryRepository imageBinaryRepository,
                               ImageGroupRepository imageGroupRepository,
                               ImageResizingService imageResizingService,
                               RuntimeSettingsService runtimeSettingsService, DefaultProfileImageLoader defaultProfileImageLoader) {
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.imageBinaryRepository = imageBinaryRepository;
        this.imageGroupRepository = imageGroupRepository;
        this.imageResizingService = imageResizingService;
        this.runtimeSettingsService = runtimeSettingsService;
        this.defaultProfileImageLoader = defaultProfileImageLoader;
    }

    public ImageGroup initUserProfileImageGroup() {
        ImageGroup imageGroup = imageGroupRepository.findByUserProfileImageGroup();
        if (imageGroup == null) {
            imageGroup = new ImageGroup(FredbetConstants.GALLERY_NAME, true);
            return imageGroupRepository.save(imageGroup);
        } else {
            return imageGroup;
        }
    }

    public ImageGroup findOrCreateImageGroup(String galleryGroupName) {
        ImageGroup imageGroup = imageGroupRepository.findByName(galleryGroupName);

        if (imageGroup == null) {
            String tmpImageGroupName = StringUtils.isBlank(galleryGroupName) ? FredbetConstants.DEFAULT_IMAGE_GROUP_NAME : galleryGroupName;
            imageGroup = new ImageGroup(tmpImageGroupName);
            imageGroupRepository.save(imageGroup);
        }
        return imageGroup;
    }

    public void saveImage(byte[] binary, String galleryGroup, String description, AppUser currentUser) {
        final byte[] thumbnail = imageResizingService.createThumbnail(binary);
        final String imageKey = imageBinaryRepository.saveImage(binary, thumbnail);

        final ImageGroup imageGroup = findOrCreateImageGroup(galleryGroup);
        checkIfImageUploadPerUserIsReached(currentUser);

        ImageMetaData imageMetaData = new ImageMetaData(imageKey, imageGroup, currentUser);
        imageMetaData.setDescription(description);
        imageMetaDataRepository.save(imageMetaData);
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }

    private void checkIfImageUploadPerUserIsReached(AppUser currentUser) {
        Integer imageUploadLimit = runtimeSettingsService.loadRuntimeSettings().getImageUploadLimit();
        if (imageUploadLimit == null) {
            // no limit
            return;
        }

        Integer alreadyUploadedImages = imageMetaDataRepository.numberOfImagesOfUser(currentUser.getUsername());
        if (alreadyUploadedImages >= imageUploadLimit) {
            throw new ImageUploadLimitReachedException("The image upload limit has been reached!", imageUploadLimit);
        }
    }

    public void saveDefaultProfileImageFor(AppUser appUser) {
        saveUserProfileImage(defaultProfileImageLoader.getDefaultProfileImage().imageBinary(), appUser, null);
    }

    public void saveUserProfileImage(byte[] binary, AppUser appUser, ImageMetaData imageMetaData) {
        final byte[] thumbnail = imageResizingService.createThumbnail(binary);
        final String imageKey = imageBinaryRepository.saveImage(binary, thumbnail);

        if (imageMetaData == null) {
            // create new user profile image
            ImageGroup imageGroup = imageGroupRepository.findByUserProfileImageGroup();
            if (imageGroup == null) {
                imageGroup = initUserProfileImageGroup();
            }

            imageMetaData = new ImageMetaData(imageKey, imageGroup, appUser);
            imageMetaData.setDescription(appUser.getUsername());
        } else {
            imageMetaData.setImageKey(imageKey);
        }

        imageMetaDataRepository.save(imageMetaData);
    }

    public List<ImageMetaData> fetchAllImages() {
        return imageMetaDataRepository.findAll();
    }

    public List<ImageMetaData> fetchAllImagesExceptUserProfileImages() {
        return imageMetaDataRepository.findImageMetaDataWithoutProfileImages();
    }

    public List<ImageMetaData> fetchImagesOfUserExceptUserProfileImages(String currentUserName) {
        LOG.debug("fetching images of user={}", currentUserName);
        return imageMetaDataRepository.findImageMetaDataForUser(currentUserName);
    }

    public BinaryImage loadImageByImageKey(String imageKey) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            return defaultProfileImageLoader.getDefaultProfileImage();
        }

        ImageBinary imageBinary = imageBinaryRepository.getReferenceById(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
    }

    public BinaryImage loadThumbnailByImageKey(String imageKey) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            return defaultProfileImageLoader.getDefaultThumbProfileImage();
        }

        ImageBinary imageBinary = imageBinaryRepository.getReferenceById(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getThumbImageBinary());
    }

    public void deleteImageByImageKey(String imageKey) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            LOG.warn("Could not found image with imageKey: {}", imageKey);
            return;
        }

        imageMetaDataRepository.delete(imageMetaData);
        imageBinaryRepository.deleteImage(imageMetaData.getImageKey());
    }

    public boolean isImageOfUser(String imageKey, AppUser appUser) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            return false;
        }

        return imageMetaData.getOwner().getId().equals(appUser.getId());
    }

    public List<String> findAvailableImageGroups() {
        return imageGroupRepository.findAllGroupsWithoutUserProfileImageGroup().stream().map(ImageGroup::getName).sorted()
            .toList();
    }

    public void createDefaultImageGroup() {
        long numberOfImageGroups = imageGroupRepository.count();
        if (numberOfImageGroups == 1) {
            // if there is only one image group then it is the users group
            findOrCreateImageGroup(FredbetConstants.DEFAULT_IMAGE_GROUP_NAME);
        }
    }

    public void deleteUserImages(AppUser appUser) {
        List<ImageMetaData> imageMetaDataList = imageMetaDataRepository.findByOwner(appUser);
        imageMetaDataList.forEach(imageMetaData -> {
            Optional<ImageBinary> imageOpt = imageBinaryRepository.findById(imageMetaData.getImageKey());
            imageOpt.ifPresent(imageBinary -> imageBinaryRepository.deleteById(imageBinary.getKey()));
        });
        imageMetaDataRepository.deleteAll(imageMetaDataList);
    }

    public void saveUserProfileImage(byte[] binary, AppUser appUser) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findImageMetaDataOfUserProfileImage(appUser.getUsername());
        saveUserProfileImage(binary, appUser, imageMetaData);
    }

    public ImageMetaData getProfileImageMetaDataFor(String username) {
        return imageMetaDataRepository.findImageMetaDataOfUserProfileImage(username);
    }
}
