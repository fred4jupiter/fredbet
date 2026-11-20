package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.team.InvalidCrestException;
import de.fred4jupiter.fredbet.team.TeamService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Downloads the crest image of a team from a URL given in a CSV file. SVG, JPEG and PNG images are supported.
 */
@Component
class CrestImageDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(CrestImageDownloader.class);

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    private static final int MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final RestClient restClient;

    private final TeamService teamService;

    CrestImageDownloader(TeamService teamService) {
        this.teamService = teamService;
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(TIMEOUT);
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    /**
     * @return true if the given value is an absolute HTTP or HTTPS URL
     */
    static boolean isValidImageUrl(String url) {
        return toUri(url).isPresent();
    }

    /**
     * Downloads the image and converts it to a crest.
     *
     * @return the crest or empty, if the image could not be downloaded or is no valid SVG, JPEG or PNG image
     */
    Optional<SvgImage> download(String url) {
        Optional<URI> uri = toUri(url);
        if (uri.isEmpty()) {
            LOG.info("Invalid crest image URL: {}", url);
            return Optional.empty();
        }

        try {
            byte[] image = restClient.get().uri(uri.get()).exchange((_, response) -> {
                if (!response.getStatusCode().is2xxSuccessful()) {
                    LOG.info("Could not download crest image from {}. Status: {}", url, response.getStatusCode());
                    return null;
                }
                try (InputStream body = response.getBody()) {
                    return body.readNBytes(MAX_IMAGE_SIZE + 1);
                }
            });

            if (image == null || image.length == 0) {
                return Optional.empty();
            }
            if (image.length > MAX_IMAGE_SIZE) {
                LOG.info("Crest image from {} is too large (max. {} bytes).", url, MAX_IMAGE_SIZE);
                return Optional.empty();
            }
            return Optional.of(teamService.convertToCrest(image));
        } catch (RestClientException | InvalidCrestException e) {
            LOG.info("Could not download crest image from {}. Cause: {}", url, e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<URI> toUri(String url) {
        if (StringUtils.isBlank(url)) {
            return Optional.empty();
        }
        try {
            URI uri = new URI(url.trim());
            if (uri.getScheme() == null || !ALLOWED_SCHEMES.contains(uri.getScheme().toLowerCase(Locale.ROOT)) || StringUtils.isBlank(uri.getHost())) {
                return Optional.empty();
            }
            return Optional.of(uri);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
