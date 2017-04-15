package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;

public interface ImageMetaDataRepository extends JpaRepository<ImageMetaData, Long> {

	List<ImageMetaData> findByOwner(AppUser appUser);
	
	ImageMetaData findByImageKey(String imageKey);

}
