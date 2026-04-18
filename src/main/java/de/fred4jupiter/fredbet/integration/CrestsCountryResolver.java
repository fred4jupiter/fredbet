package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.Country;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class CrestsCountryResolver {

    public Optional<byte[]> loadCrestsImageFor(Country country) {
        // TODO implement me
        return Optional.empty();
    }
}
