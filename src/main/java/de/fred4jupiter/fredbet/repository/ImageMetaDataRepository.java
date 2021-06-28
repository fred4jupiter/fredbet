package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ImageMetaDataRepository extends JpaRepository<ImageMetaData, Long> {

    @Query("select a from ImageMetaData a where a.owner.username = :username and a.imageGroup.userProfileImageGroup = true")
    ImageMetaData findImageMetaDataOfUserProfileImage(@Param("username") String username);

    @Query("delete from ImageMetaData a where a.owner.id = :userId")
    @Modifying
    @Transactional
    void deleteMetaDataByOwner(@Param("userId") Long userId);

    ImageMetaData findByImageKey(String imageKey);

    @Query("select a from ImageMetaData a where a.imageGroup.userProfileImageGroup = true")
    List<ImageMetaData> loadImageMetaDataOfUserProfileImageSet();

    @Query("select a from ImageMetaData a where a.imageGroup.userProfileImageGroup = false")
    List<ImageMetaData> findImageMetaDataWithoutProfileImages();

    @Query("select a from ImageMetaData a where a.owner.username = :username and a.imageGroup.userProfileImageGroup = false")
    List<ImageMetaData> findImageMetaDataForUser(@Param("username") String username);

    ImageMetaData findByOwnerAndImageGroup(AppUser owner, ImageGroup imageGroup);

}
