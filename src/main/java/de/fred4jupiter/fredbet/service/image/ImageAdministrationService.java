package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;
import de.fred4jupiter.fredbet.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageAdministrationService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageAdministrationService.class);

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final ImageGroupRepository imageGroupRepository;

    private final ImageResizingService imageResizingService;

    private final ImageLocationStrategy imageLocationStrategy;

    private final ImageKeyGenerator imageKeyGenerator;

    private final SecurityService securityService;

    private final UserService userService;

    public ImageAdministrationService(ImageMetaDataRepository imageMetaDataRepository, ImageGroupRepository imageGroupRepository,
                                      ImageResizingService imageResizingService, ImageLocationStrategy imageLocationStrategy,
                                      ImageKeyGenerator imageKeyGenerator, SecurityService securityService,
                                      UserService userService) {
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.imageGroupRepository = imageGroupRepository;
        this.imageResizingService = imageResizingService;
        this.imageLocationStrategy = imageLocationStrategy;
        this.imageKeyGenerator = imageKeyGenerator;
        this.securityService = securityService;
        this.userService = userService;
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

    public void saveImage(byte[] binary, Long imageGroupId, String description) {
        final ImageGroup imageGroup = imageGroupRepository.getById(imageGroupId);

        final String key = imageKeyGenerator.generateKey();

        ImageMetaData image = new ImageMetaData(key, imageGroup, securityService.getCurrentUser());
        image.setDescription(description);
        imageMetaDataRepository.save(image);

        byte[] thumbnail = imageResizingService.createThumbnail(binary);

        imageLocationStrategy.saveImage(key, imageGroup.getId(), binary, thumbnail);
    }

    public void saveUserProfileImage(byte[] binary) {
        final AppUser appUser = userService.findByUserId(securityService.getCurrentUser().getId());
        ImageMetaData imageMetaData = securityService.getCurrentUserProfileImageMetaData();
        saveUserProfileImage(binary, appUser, imageMetaData);
    }

    public void saveUserProfileImage(byte[] binary, AppUser appUser) {
        saveUserProfileImage(binary, appUser, null);
    }

    private void saveUserProfileImage(byte[] binary, AppUser appUser, ImageMetaData imageMetaData) {
        final String key = imageKeyGenerator.generateKey();
        if (imageMetaData == null) {
            // create new user profile image
            ImageGroup imageGroup = imageGroupRepository.findByUserProfileImageGroup();
            if (imageGroup == null) {
                imageGroup = initUserProfileImageGroup();
            }

            imageMetaData = new ImageMetaData(key, imageGroup, appUser);
            imageMetaData.setDescription(appUser.getUsername());
        } else {
            imageMetaData.setImageKey(key);
        }

        imageMetaDataRepository.save(imageMetaData);

        byte[] thumbnail = imageResizingService.createThumbnail(binary);
        imageLocationStrategy.saveImage(key, imageMetaData.getImageGroup().getId(), binary, thumbnail);
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
            return null;
        }

        return imageLocationStrategy.getImageByKey(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
    }

    public BinaryImage loadThumbnailByImageKey(String imageKey) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            return null;
        }

        return imageLocationStrategy.getThumbnailByKey(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
    }

    public void deleteImageByImageKey(String imageKey) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            LOG.warn("Could not found image with imageKey: {}", imageKey);
            return;
        }

        imageMetaDataRepository.delete(imageMetaData);
        imageLocationStrategy.deleteImage(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
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
                .collect(Collectors.toList());
    }

    public void createDefaultImageGroup() {
        long numberOfImageGroups = imageGroupRepository.count();
        if (numberOfImageGroups == 1) {
            // if there is only one image group then it is the users group
            findOrCreateImageGroup(FredbetConstants.DEFAULT_IMAGE_GROUP_NAME);
        }
    }
}
