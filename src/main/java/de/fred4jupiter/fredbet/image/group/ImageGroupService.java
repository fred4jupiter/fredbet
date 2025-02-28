package de.fred4jupiter.fredbet.image.group;

import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageGroupService {

    private final ImageGroupRepository imageGroupRepository;

    public ImageGroupService(ImageGroupRepository imageGroupRepository) {
        this.imageGroupRepository = imageGroupRepository;
    }

    public List<ImageGroup> findAvailableImageGroups() {
        return imageGroupRepository.findAll();
    }

    public void deleteImageGroup(Long imageGroupId) {
        imageGroupRepository.findById(imageGroupId).ifPresent(group -> {
            if (group.isUserProfileImageGroup() || group.isDefaultImageGroup()) {
                throw new ImageGroupNotDeletableException("Image Group with ID=" + imageGroupId + " will be used as user profile image group and cannot be deleted!");
            }
        });

        imageGroupRepository.deleteById(imageGroupId);
    }

    public void addImageGroup(String imageGroupName) {
        checkImageGroupName(imageGroupName);
        imageGroupRepository.save(new ImageGroup(imageGroupName));
    }

    public void updateImageGroup(Long id, String name) {
        Optional<ImageGroup> imageGroupOpt = imageGroupRepository.findById(id);
        if (imageGroupOpt.isEmpty()) {
            throw new IllegalArgumentException("Image group with id=" + id + " could not be found!");
        }

        checkImageGroupName(name);

        ImageGroup imageGroup = imageGroupOpt.get();
        imageGroup.setName(name);
        imageGroupRepository.save(imageGroup);
    }

    private void checkImageGroupName(String imageGroupName) {
        ImageGroup foundImageGroup = imageGroupRepository.findByName(imageGroupName);
        if (foundImageGroup != null) {
            throw new ImageGroupExistsException("An image with this group name still exists!");
        }
    }

}
