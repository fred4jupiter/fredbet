package de.fred4jupiter.fredbet.service.image;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;
import de.fred4jupiter.fredbet.web.image.ImageGroupCommand;

@Service
public class ImageGroupService {

	@Autowired
	private ImageGroupRepository imageGroupRepository;

	public List<ImageGroupCommand> findAvailableImageGroups() {
		List<ImageGroup> imageGroups = imageGroupRepository.findAll();

		return imageGroups.stream().map(imageGroup -> mapToImageGroupCommand(imageGroup)).collect(Collectors.toList());
	}

	public void deleteImageGroup(Long imageGroupId) {
		imageGroupRepository.delete(imageGroupId);
	}

	public void addImageGroup(String imageGroupName) {
		checkImageGroupName(imageGroupName);
		imageGroupRepository.save(new ImageGroup(imageGroupName));
	}

	public void updateImageGroup(ImageGroupCommand imageGroupCommand) {
		ImageGroup imageGroup = imageGroupRepository.findOne(imageGroupCommand.getId());
		if (imageGroup == null) {
			throw new IllegalArgumentException("Image group with id=" + imageGroupCommand.getId() + " could not be found!");
		}

		checkImageGroupName(imageGroupCommand.getName());

		imageGroup.setName(imageGroupCommand.getName());
		imageGroupRepository.save(imageGroup);
	}

	private void checkImageGroupName(String imageGroupName) {
		ImageGroup foundImageGroup = imageGroupRepository.findByName(imageGroupName);
		if (foundImageGroup != null) {
			throw new ImageGroupExistsException("An image with this group name still exists!");
		}
	}

	private ImageGroupCommand mapToImageGroupCommand(ImageGroup imageGroup) {
		ImageGroupCommand imageGroupCommand = new ImageGroupCommand();
		imageGroupCommand.setId(imageGroup.getId());
		imageGroupCommand.setName(imageGroup.getName());
		return imageGroupCommand;
	}

}
