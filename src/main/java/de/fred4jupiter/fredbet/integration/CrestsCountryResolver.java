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
        if (StringUtils.isNotBlank(country.getFlagIconCode())) {
            Optional<byte[]> imageOpt = loadByCode(country.getFlagIconCode());
            if (imageOpt.isPresent()) {
                return imageOpt;
            }
        }

        Optional<String> alpha2CodeOpt = resolveToAlpha2Code(country);
        if (alpha2CodeOpt.isPresent()) {
            Optional<byte[]> imageOpt = loadByCode(alpha2CodeOpt.get());
            if (imageOpt.isPresent()) {
                return imageOpt;
            }
        }

        LOG.warn("Could not load crests image for country={}", country);
        return Optional.empty();
    }

    private Optional<byte[]> loadByCode(String code) {
        String fileName = "%s.svg".formatted(code);
        byte[] bytes = loadResourceByFilename(fileName);
        return bytes != null ? Optional.of(bytes) : Optional.empty();
    }

    private byte[] loadResourceByFilename(String filename) {
        Resource resource = resourceLoader.getResource("classpath:static/flag-icons-7.2.3/flags/4x3/%s".formatted(filename));
        if (resource.exists()) {
            try {
                return resource.getContentAsByteArray();
            } catch (IOException e) {
                LOG.error("Could not found flag image file filename={}", filename);
                return null;
            }
        }
        return null;
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
