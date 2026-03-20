package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageBinaryRepository extends JpaRepository<ImageBinary, String> {

    default String saveImage(byte[] imageBytes, byte[] thumbImageBinary) {
        ImageBinary imageBinary = new ImageBinary(imageBytes, thumbImageBinary);
        ImageBinary savedImage = save(imageBinary);
        return savedImage.getKey();
    }

    @Modifying
    @Query("delete ImageBinary i where i.key = :imageKey")
    int deleteImage(@Param("imageKey") String imageKey);

}
