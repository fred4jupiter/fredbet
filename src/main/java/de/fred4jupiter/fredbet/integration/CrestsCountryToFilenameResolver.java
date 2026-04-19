package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.Country;
import org.springframework.stereotype.Component;

@Component
class CrestsCountryToFilenameResolver {

    public String mapCountryToFilename(Country country) {
        if (country == null) {
            return null;
        }

        return switch (country) {
            case INTER_MIAMI -> "inter-miami-cf.svg";
            case MANCHESTER_CITY -> "manchester-city.svg";
            default -> null;
        };
    }
}
