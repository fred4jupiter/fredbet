package de.fred4jupiter.fredbet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.ImageBinary;

public interface ImageBinaryRepository extends JpaRepository<ImageBinary, String> {

	ImageBinary findByKeyAndImageGroupId(String imageKey, Long imageGroupId);

}
