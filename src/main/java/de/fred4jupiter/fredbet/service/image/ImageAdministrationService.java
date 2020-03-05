package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.web.image.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageAdministrationService {

    private static final String DEFAULT_IMAGE_GROUP_NAME = "Misc";

    private static final Logger LOG = LoggerFactory.getLogger(ImageAdministrationService.class);

    @Autowired
    private ImageMetaDataRepository imageMetaDataRepository;

    @Autowired
    private ImageGroupRepository imageGroupRepository;

    @Autowired
    private ImageResizingService imageResizingService;

    @Autowired
    private ImageLocationStrategy imageLocationService;

    @Autowired
    private ImageKeyGenerator imageKeyGenerator;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    private static final String GALLERY_NAME = "Users";

    @PostConstruct
    private void initUserProfileImageGroup() {
        ImageGroup imageGroup = imageGroupRepository.findByUserProfileImageGroup();

        if (imageGroup == null) {
            imageGroup = new ImageGroup(GALLERY_NAME, true);
            imageGroupRepository.save(imageGroup);
        }
    }

    public ImageGroup createOrFetchImageGroup(String galleryGroupName) {
        ImageGroup imageGroup = imageGroupRepository.findByName(galleryGroupName);

        if (imageGroup == null) {
            imageGroup = new ImageGroup(galleryGroupName);
            imageGroupRepository.save(imageGroup);
        }
        return imageGroup;
    }

    public void saveImage(byte[] binary, Long imageGroupId, String description, Rotation rotation) {
        final ImageGroup imageGroup = imageGroupRepository.getOne(imageGroupId);

        final String key = imageKeyGenerator.generateKey();

        ImageMetaData image = new ImageMetaData(key, imageGroup, securityService.getCurrentUser());
        image.setDescription(description);
        imageMetaDataRepository.save(image);

        byte[] thumbnail = imageResizingService.createThumbnail(binary, rotation);
        byte[] imageByte = imageResizingService.minimizeToDefaultSize(binary, rotation);

        imageLocationService.saveImage(key, imageGroup.getId(), imageByte, thumbnail);
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
            final ImageGroup imageGroup = imageGroupRepository.findByUserProfileImageGroup();
            imageMetaData = new ImageMetaData(key, imageGroup, appUser);
            imageMetaData.setDescription(appUser.getUsername());
        } else {
            imageMetaData.setImageKey(key);
        }

        imageMetaDataRepository.save(imageMetaData);

        byte[] thumbnail = imageResizingService.createThumbnail(binary, Rotation.NONE);
        byte[] imageByte = imageResizingService.minimizeToDefaultSize(binary, Rotation.NONE);
        imageLocationService.saveImage(key, imageMetaData.getImageGroup().getId(), imageByte, thumbnail);
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

        return imageLocationService.getImageByKey(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
    }

    public BinaryImage loadThumbnailByImageKey(String imageKey) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            return null;
        }

        return imageLocationService.getThumbnailByKey(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
    }

    public void deleteImageByImageKey(String imageKey) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            LOG.warn("Could not found image with imageKey: {}", imageKey);
            return;
        }

        imageMetaDataRepository.delete(imageMetaData);
        imageLocationService.deleteImage(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
    }

    public boolean isImageOfUser(String imageKey, AppUser appUser) {
        ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
        if (imageMetaData == null) {
            return false;
        }

        if (imageMetaData.getOwner().getId().equals(appUser.getId())) {
            return true;
        }

        return false;
    }

    public List<String> findAvailableImageGroups() {
        return imageGroupRepository.findAllGroupsWithoutUserProfileImageGroup().stream().map(ImageGroup::getName).sorted()
                .collect(Collectors.toList());
    }

    public void createDefaultImageGroup() {
        long numberOfImageGroups = imageGroupRepository.count();
        if (numberOfImageGroups == 1) {
            // if there is only one image group then it is the users group
            createOrFetchImageGroup(DEFAULT_IMAGE_GROUP_NAME);
        }
    }
}
