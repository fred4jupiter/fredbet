package de.fred4jupiter.fredbet.service.image;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;

@Service
public class ImageGroupService {

	@Autowired
	private ImageGroupRepository imageGroupRepository;

	public List<ImageGroup> findAvailableImageGroups() {
		return imageGroupRepository.findAll();
	}

	public void deleteImageGroup(Long imageGroupId) {
		imageGroupRepository.deleteById(imageGroupId);
	}

	public void addImageGroup(String imageGroupName) {
		checkImageGroupName(imageGroupName);
		imageGroupRepository.save(new ImageGroup(imageGroupName));
	}

	public void updateImageGroup(Long id, String name) {
		Optional<ImageGroup> imageGroupOpt = imageGroupRepository.findById(id);
		if (!imageGroupOpt.isPresent()) {
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
