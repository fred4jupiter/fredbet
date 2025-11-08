package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

interface ImageBinaryRepository extends JpaRepository<ImageBinary, String> {

    default List<BinaryImage> findAllImages() {
        List<ImageBinary> allImages = findAll();
        if (allImages.isEmpty()) {
            return Collections.emptyList();
        }
        return allImages.stream().map(this::toImageData).toList();
    }

    default void saveImage(String imageKey, byte[] imageBytes, byte[] thumbImageBinary) {
        ImageBinary imageBinary = new ImageBinary(imageKey, imageBytes, thumbImageBinary);
        save(imageBinary);
    }

    default void deleteImage(String imageKey) {
        Optional<ImageBinary> binaryOpt = findById(imageKey);
        binaryOpt.ifPresent(this::delete);
    }

    default BinaryImage getImageByKey(String imageKey) {
        ImageBinary imageBinary = findOne(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
    }

    private ImageBinary findOne(String imageKey) {
        Optional<ImageBinary> imageBinaryOpt = findById(imageKey);
        return imageBinaryOpt.orElse(null);
    }

    default BinaryImage getThumbnailByKey(String imageKey) {
        ImageBinary imageBinary = findOne(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getThumbImageBinary());
    }

    private BinaryImage toImageData(ImageBinary imageBinary) {
        return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
    }
}
