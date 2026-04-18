package de.fred4jupiter.fredbet.integration;

import com.neovisionaries.i18n.CountryCode;
import de.fred4jupiter.fredbet.domain.Country;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
class CrestsCountryResolver {

    private static final Logger LOG = LoggerFactory.getLogger(CrestsCountryResolver.class);

    private final ResourceLoader resourceLoader;

    CrestsCountryResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Optional<byte[]> loadCrestsImageFor(Country country) {
        Optional<String> alpha2CodeOpt = resolveToAlpha2Code(country);
        if (alpha2CodeOpt.isEmpty()) {
            LOG.warn("missing alpha 2 code for country={}", country);
            return Optional.empty();
        }

        String fileName = "%s.svg".formatted(alpha2CodeOpt.get());
        Resource resource = resourceLoader.getResource("classpath:static/flag-icons-7.2.3/flags/4x3/%s".formatted(fileName));
        if (resource.exists()) {
            try {
                return Optional.of(resource.getContentAsByteArray());
            } catch (IOException e) {
                LOG.error("Could not load crests image for country={}", country);
                return Optional.empty();
            }
        }

        // TODO implement me
        return Optional.empty();
    }

    private Optional<String> resolveToAlpha2Code(Country country) {
        if (StringUtils.isBlank(country.getAlpha3Code())) {
            return Optional.empty();
        }

        String alpha3 = country.getAlpha3Code().toUpperCase();
        CountryCode countryCode = CountryCode.getByAlpha3Code(alpha3);
        if (countryCode != null && countryCode.getAlpha2() != null) {
            return Optional.of(countryCode.getAlpha2());
        }
        return Optional.empty();
    }


}
