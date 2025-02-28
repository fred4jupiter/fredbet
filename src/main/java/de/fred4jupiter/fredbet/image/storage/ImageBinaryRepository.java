package de.fred4jupiter.fredbet.image.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;

public interface ImageBinaryRepository extends JpaRepository<ImageBinary, String> {

	ImageBinary findByKeyAndImageGroupId(String imageKey, Long imageGroupId);

}
