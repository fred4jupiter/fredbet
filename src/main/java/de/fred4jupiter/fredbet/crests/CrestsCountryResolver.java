package de.fred4jupiter.fredbet.crests;

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
public class CrestsCountryResolver {

    private static final Logger LOG = LoggerFactory.getLogger(CrestsCountryResolver.class);

    private static final String COUNTRY_FLAGS_CLASSPATH_LOCATION = "classpath:static/flag-icons-7.2.3/flags/4x3";

    private static final String CLUB_WM_FLAGS_CLASSPATH_LOCATION = "classpath:static/club-wm-icons/flags";

    private final ResourceLoader resourceLoader;

    CrestsCountryResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Optional<byte[]> loadCrestsImageFor(Country country) {
        if (StringUtils.isNotBlank(country.getCssIconClass())) {
            Optional<byte[]> flagOpt = loadClubWmIcon(country);
            if (flagOpt.isPresent()) {
                return flagOpt;
            } else {
                LOG.warn("Could not load crests image for CLUB WM country={}", country);
                return Optional.empty();
            }
        }

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

    private Optional<byte[]> loadClubWmIcon(Country country) {
        Optional<byte[]> imageOpt = loadClubWmIconByCssIconClass(country.getCssIconClass());
        if (imageOpt.isPresent()) {
            return imageOpt;
        }

        String filename = mapClubWmCountryToFilename(country);
        return Optional.ofNullable(loadResourceByFilename(filename, CLUB_WM_FLAGS_CLASSPATH_LOCATION));
    }

    private String mapClubWmCountryToFilename(Country country) {
        if (country == null) {
            return null;
        }

        return switch (country) {
            case INTER_MIAMI -> "inter-miami-cf.svg";
            case MANCHESTER_CITY -> "manchester-city.svg";
            default -> null;
        };
    }

    private Optional<byte[]> loadClubWmIconByCssIconClass(String cssIconClass) {
        String filenameWithoutExtension = StringUtils.substringAfter(cssIconClass, "kwm kwm-");
        String fileName = "%s.svg".formatted(filenameWithoutExtension).toLowerCase();
        return Optional.ofNullable(loadResourceByFilename(fileName, CLUB_WM_FLAGS_CLASSPATH_LOCATION));
    }

    private Optional<byte[]> loadByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return Optional.empty();
        }

        String fileName = "%s.svg".formatted(code).toLowerCase();
        byte[] bytes = loadResourceByFilename(fileName, COUNTRY_FLAGS_CLASSPATH_LOCATION);
        return bytes != null ? Optional.of(bytes) : Optional.empty();
    }

    private byte[] loadResourceByFilename(String filename, String classpathLocation) {
        String pathFilename = "%s/%s".formatted(classpathLocation, filename);
        Resource resource = resourceLoader.getResource(pathFilename);
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
