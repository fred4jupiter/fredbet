package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ImageBinaryRepository extends JpaRepository<ImageBinary, String> {

    default void saveImage(String imageKey, byte[] imageBytes, byte[] thumbImageBinary) {
        ImageBinary imageBinary = new ImageBinary(imageKey, imageBytes, thumbImageBinary);
        save(imageBinary);
    }

    @Modifying
    @Query("delete ImageBinary i where i.key = :imageKey")
    int deleteImage(@Param("imageKey") String imageKey);

    default BinaryImage getImageByKey(String imageKey) {
        ImageBinary imageBinary = getReferenceById(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
    }

    default BinaryImage getThumbnailByKey(String imageKey) {
        ImageBinary imageBinary = getReferenceById(imageKey);
        return new BinaryImage(imageBinary.getKey(), imageBinary.getThumbImageBinary());
    }
}
