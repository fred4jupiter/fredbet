package de.fred4jupiter.fredbet.crests;

import de.fred4jupiter.fredbet.country.CountryResolver;
import de.fred4jupiter.fredbet.domain.ClubTeam;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class CrestsCountryResolver {

    private static final Logger LOG = LoggerFactory.getLogger(CrestsCountryResolver.class);

    private static final String COUNTRY_FLAGS_CLASSPATH_LOCATION = "classpath:static/flag-icons-7.2.3/flags/4x3";

    private static final String CLUB_WM_FLAGS_CLASSPATH_LOCATION = "classpath:static/club-wm-icons/flags";

    private final ResourceLoader resourceLoader;

    private final CrestPlaceholderLoader crestPlaceholderLoader;

    private final CountryResolver countryAlpha2Codes;

    CrestsCountryResolver(ResourceLoader resourceLoader, CrestPlaceholderLoader crestPlaceholderLoader, CountryResolver countryAlpha2Codes) {
        this.resourceLoader = resourceLoader;
        this.crestPlaceholderLoader = crestPlaceholderLoader;
        this.countryAlpha2Codes = countryAlpha2Codes;
    }

    public SvgImage loadCrestsImageFor(TeamBundleEntry teamBundleEntry) {
        return loadCrestsImageFor(teamBundleEntry, true)
            .orElseThrow(() -> new IllegalStateException("Could not load crests image for teamBundleEntry=" + teamBundleEntry));
    }

    public Optional<SvgImage> loadCrestsImageFor(TeamBundleEntry teamBundleEntry, boolean fallbackToPlaceholderIcon) {
        Optional<SvgImage> imageOpt = switch (teamBundleEntry) {
            case null -> Optional.empty();
            case Country country -> loadCountryFlag(country);
            case ClubTeam clubTeam -> loadClubTeamCrest(clubTeam);
            default -> throw new IllegalArgumentException("Unsupported team bundle entry: " + teamBundleEntry);
        };

        if (imageOpt.isPresent()) {
            return imageOpt;
        }

        return fallbackToPlaceholderIcon ? useFallbackPlaceholderIcon(teamBundleEntry) : Optional.empty();
    }

    private Optional<SvgImage> loadCountryFlag(Country country) {
        if (StringUtils.isNotBlank(country.getFlagIconCode())) {
            Optional<SvgImage> imageOpt = loadByCode(country.getFlagIconCode());
            if (imageOpt.isPresent()) {
                return imageOpt;
            }
        }

        return resolveToAlpha2Code(country).flatMap(this::loadByCode);
    }

    private Optional<SvgImage> loadClubTeamCrest(ClubTeam clubTeam) {
        String fileName = "%s.svg".formatted(clubTeam.getCrestFileName());
        return Optional.ofNullable(loadResourceByFilename(fileName, CLUB_WM_FLAGS_CLASSPATH_LOCATION));
    }

    private Optional<SvgImage> useFallbackPlaceholderIcon(TeamBundleEntry teamBundleEntry) {
        if (teamBundleEntry != null) {
            LOG.warn("Could not load crests image for teamBundleEntry={}. Using fallback crests icon...", teamBundleEntry);
        }

        return Optional.of(crestPlaceholderLoader.getCrestPlaceholderIcon());
    }

    private Optional<SvgImage> loadByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return Optional.empty();
        }

        String fileName = "%s.svg".formatted(code).toLowerCase();
        return Optional.ofNullable(loadResourceByFilename(fileName, COUNTRY_FLAGS_CLASSPATH_LOCATION));
    }

    private SvgImage loadResourceByFilename(String filename, String classpathLocation) {
        String pathFilename = "%s/%s".formatted(classpathLocation, filename);
        Resource resource = resourceLoader.getResource(pathFilename);
        if (resource.exists()) {
            try {
                return new SvgImage(resource.getContentAsString(StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOG.error("Could not found flag image file filename={}", filename);
                return null;
            }
        }
        return null;
    }

    private Optional<String> resolveToAlpha2Code(Country country) {
        return Optional.ofNullable(countryAlpha2Codes.countryToAlpha2Code(country));
    }
}
