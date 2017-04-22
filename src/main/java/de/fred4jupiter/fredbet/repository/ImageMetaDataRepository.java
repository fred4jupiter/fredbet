package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;

public interface ImageMetaDataRepository extends JpaRepository<ImageMetaData, Long> {

	List<ImageMetaData> findByOwner(AppUser appUser);

	@Query("select a from ImageMetaData a where a.owner.username = :username and a.imageGroup.userProfileImageGroup = true")
	ImageMetaData findImageMetaDataOfUserProfileImage(@Param("username") String username);

	ImageMetaData findByImageKey(String imageKey);

	@Query("select a from ImageMetaData a where a.imageGroup.userProfileImageGroup = true")
	List<ImageMetaData> loadImageMetaDataOfUserProfileImageSet();

	@Query("select a from ImageMetaData a where a.imageGroup.userProfileImageGroup = false")
	List<ImageMetaData> findImageMetaDataWithoutProfileImages();

	@Query("select a from ImageMetaData a where a.owner.username = :username and a.imageGroup.userProfileImageGroup = false")
	List<ImageMetaData> findImageMetaDataForUser(@Param("username") String username);
}
