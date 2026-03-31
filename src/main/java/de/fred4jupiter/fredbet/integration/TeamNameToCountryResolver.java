package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.integration.model.FdTeam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component
class TeamNameToCountryResolver {

    private static final Logger LOG = LoggerFactory.getLogger(TeamNameToCountryResolver.class);

    private final Properties countryProps;

    public TeamNameToCountryResolver(@Value("classpath:/msgs/TeamKey_en.properties") Resource countryNameResource) {
        this.countryProps = loadCountryNames(countryNameResource);
    }

    public Country resolveToCountry(FdTeam team) {
        if (team == null || team.tla() == null || team.name() == null) {
            return null;
        }

        Country country = Country.fromAlpha3Code(team.tla().toLowerCase());
        if (country != null) {
            return country;
        }

        if (countryProps.containsValue(team.name())) {
            String key = getKeyByValue(countryProps, team.name());
            if (StringUtils.isNotBlank(key)) {
                String alpha3IsoCode = Strings.CS.remove(key, "country.");
                return Country.fromAlpha3Code(alpha3IsoCode);
            }
        }

        LOG.warn("Could not resolve country for team '{}'.", team);
        return null;
    }

    private String getKeyByValue(Properties countryProps, String name) {
        Set<Map.Entry<Object, Object>> entries = countryProps.entrySet();

        for (Map.Entry<Object, Object> next : entries) {
            if (next.getValue().equals(name)) {
                return (String) next.getKey();
            }
        }

        return null;
    }

    private Properties loadCountryNames(Resource countryNameResource) {
        try (final InputStream in = countryNameResource.getInputStream()) {
            final Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
