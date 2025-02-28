package de.fred4jupiter.fredbet.image.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.fred4jupiter.fredbet.domain.entity.ImageGroup;

public interface ImageGroupRepository extends JpaRepository<ImageGroup, Long> {

	ImageGroup findByName(String name);

	@Query("select a from ImageGroup a where a.userProfileImageGroup = true")
	ImageGroup findByUserProfileImageGroup();

	@Query("select a from ImageGroup a where a.userProfileImageGroup = false")
	List<ImageGroup> findAllGroupsWithoutUserProfileImageGroup();

}
