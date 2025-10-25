package de.fred4jupiter.fredbet.data;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
class KickOffDateCreator {

    private LocalDateTime kickOffDate = LocalDateTime.now();

    public LocalDateTime createNextKickOffDate() {
        return createNextKickOffDate(1);
    }

    public LocalDateTime createNextKickOffDate(int hours) {
        this.kickOffDate = kickOffDate.plusHours(hours);
        return this.kickOffDate;
    }
}
