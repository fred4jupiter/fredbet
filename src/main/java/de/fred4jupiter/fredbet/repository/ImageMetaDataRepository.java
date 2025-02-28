package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageMetaDataRepository extends JpaRepository<ImageMetaData, Long> {

    @Query("select a from ImageMetaData a where a.owner.username = :username and a.imageGroup.userProfileImageGroup = true")
    ImageMetaData findImageMetaDataOfUserProfileImage(@Param("username") String username);

    ImageMetaData findByImageKey(String imageKey);

    @Query("select a from ImageMetaData a where a.imageGroup.userProfileImageGroup = true")
    List<ImageMetaData> loadImageMetaDataOfUserProfileImageSet();

    @Query("select a from ImageMetaData a where a.imageGroup.userProfileImageGroup = false")
    List<ImageMetaData> findImageMetaDataWithoutProfileImages();

    @Query("select a from ImageMetaData a where a.owner.username = :username and a.imageGroup.userProfileImageGroup = false")
    List<ImageMetaData> findImageMetaDataForUser(@Param("username") String username);

    ImageMetaData findByOwnerAndImageGroup(AppUser owner, ImageGroup imageGroup);

    List<ImageMetaData> findByOwner(AppUser owner);

    @Query("""
            select count(i)
            from ImageMetaData i
            where i.owner.username = :username
    """)
    Integer numberOfImagesOfUser(@Param("username") String username);

}
