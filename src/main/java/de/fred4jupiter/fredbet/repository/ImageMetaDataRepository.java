package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;

public interface ImageMetaDataRepository extends JpaRepository<ImageMetaData, Long> {

	List<ImageMetaData> findByOwner(AppUser appUser);

	@Query("select a from ImageMetaData a where a.owner.username = :username and a.imageGroup.id = :imageGroupId")
	ImageMetaData findByUsernameAndImageGroupId(@Param("username") String username, @Param("imageGroupId") Long imageGroupId);

	ImageMetaData findByImageKey(String imageKey);

	@Query("select a from ImageMetaData a where a.imageGroup.id = :imageGroupId")
	List<ImageMetaData> loadImageMetaDataOfUserProfileImageSet(@Param("imageGroupId") Long imageGroupId);
}
