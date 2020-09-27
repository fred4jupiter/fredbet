package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.domain.InfoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info, InfoPK> {

}
