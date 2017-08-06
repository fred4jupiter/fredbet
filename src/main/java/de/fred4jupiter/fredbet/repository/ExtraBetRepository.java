package de.fred4jupiter.fredbet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.ExtraBet;

public interface ExtraBetRepository extends JpaRepository<ExtraBet, Long>{

	ExtraBet findByUserName(String userName);

	@Modifying
	@Query("update ExtraBet b set b.userName = :newUsername where b.userName = :oldUsername")
	void renameUser(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);

}
