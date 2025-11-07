package de.fred4jupiter.fredbet.image.storage;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageBinaryRepository extends JpaRepository<ImageBinary, String> {

}
