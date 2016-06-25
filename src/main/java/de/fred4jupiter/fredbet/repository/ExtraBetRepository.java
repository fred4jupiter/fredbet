package de.fred4jupiter.fredbet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.ExtraBet;

public interface ExtraBetRepository extends JpaRepository<ExtraBet, Long>{

	ExtraBet findByUserName(String userName);


}
