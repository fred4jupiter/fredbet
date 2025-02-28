package de.fred4jupiter.fredbet.info;

import de.fred4jupiter.fredbet.domain.entity.Info;
import de.fred4jupiter.fredbet.domain.entity.InfoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info, InfoPK> {

}
