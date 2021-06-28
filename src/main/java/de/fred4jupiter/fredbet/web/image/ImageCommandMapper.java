package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.domain.ImageMetaData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
class ImageCommandMapper {

    public List<ImageCommand> toListOfImageCommand(List<ImageMetaData> imageMetaDataList) {
        if (imageMetaDataList.isEmpty()) {
            return Collections.emptyList();
        }

        return imageMetaDataList.stream().map(this::toImageCommand).sorted(Comparator.comparing(ImageCommand::getGalleryGroup)).collect(Collectors.toList());
    }

    private ImageCommand toImageCommand(ImageMetaData imageMetaData) {
        ImageCommand imageCommand = new ImageCommand();
        imageCommand.setImageId(imageMetaData.getId());
        imageCommand.setImageKey(imageMetaData.getImageKey());
        imageCommand.setDescription(imageMetaData.getDescription());
        imageCommand.setGalleryGroup(imageMetaData.getImageGroup().getName());
        return imageCommand;
    }
}
