package de.fred4jupiter.fredbet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long>{

}
