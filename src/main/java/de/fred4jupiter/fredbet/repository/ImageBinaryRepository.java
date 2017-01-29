package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.ImageBinary;

public interface ImageBinaryRepository extends JpaRepository<ImageBinary, String>{

	List<ImageBinary> findByImageGroup(String imageGroup);

}
