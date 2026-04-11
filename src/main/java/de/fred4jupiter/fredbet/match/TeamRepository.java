package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
