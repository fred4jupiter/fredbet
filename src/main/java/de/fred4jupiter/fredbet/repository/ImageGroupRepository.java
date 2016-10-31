package de.fred4jupiter.fredbet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.ImageGroup;

public interface ImageGroupRepository extends JpaRepository<ImageGroup, Long> {

	ImageGroup findByName(String name);

}
