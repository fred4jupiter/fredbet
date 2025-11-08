package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Storing images of the image gallery in database.
 *
 * @author michael
 */
@Component
public class DatabaseImageLocationStrategy implements ImageLocationStrategy {

    private final ImageBinaryRepository imageBinaryRepository;

    public DatabaseImageLocationStrategy(ImageBinaryRepository imageBinaryRepository) {
        super();
        this.imageBinaryRepository = imageBinaryRepository;
    }

    @Override
    public BinaryImage getImageByKey(String imageKey, Long imageGroupId) {
        ImageBinary imageBinary = findOne(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
    }

    private ImageBinary findOne(String imageKey) {
        Optional<ImageBinary> imageBinaryOpt = imageBinaryRepository.findById(imageKey);
        return imageBinaryOpt.orElse(null);
    }

    @Override
    public BinaryImage getThumbnailByKey(String imageKey, Long imageGroupId) {
        ImageBinary imageBinary = findOne(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getThumbImageBinary());
    }

    @Override
    public void deleteImage(String imageKey, Long imageGroupId) {
        Optional<ImageBinary> binaryOpt = imageBinaryRepository.findById(imageKey);
        binaryOpt.ifPresent(imageBinaryRepository::delete);
    }

}
