package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collections;
import java.util.List;

public interface ImageBinaryRepository extends JpaRepository<ImageBinary, String> {

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

    private BinaryImage toImageData(ImageBinary imageBinary) {
        return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
    }
}
