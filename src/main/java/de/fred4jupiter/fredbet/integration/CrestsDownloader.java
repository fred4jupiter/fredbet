package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.domain.SvgImage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

@Component
public class CrestsDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(CrestsDownloader.class);

    private final RestClient crestsRestClient;

    private final CrestPlaceholderLoader crestPlaceholderLoader;

    CrestsDownloader(RestClient crestsRestClient, CrestPlaceholderLoader crestPlaceholderLoader) {
        this.crestsRestClient = crestsRestClient;
        this.crestPlaceholderLoader = crestPlaceholderLoader;
    }

    public SvgImage downloadCrestsByUrl(String teamId) {
        if (StringUtils.isBlank(teamId)) {
            LOG.info("teamId is blank. No crest will be downloaded. Will use placeholder icon.");
            return crestPlaceholderLoader.getCrestPlaceholderIcon();
        }

        LOG.debug("will try to download crests with teamId: {}", teamId);

        try {
            // Request raw bytes for the SVG. Some servers return content-type `image/svg+xml` which
            // may not be handled by the String message converter. We added a ByteArrayHttpMessageConverter
            // for the crests RestClient in the config - request byte[] and convert to String with UTF-8.
            byte[] svgBytes = crestsRestClient.get().uri("/%s.svg".formatted(teamId)).retrieve().body(byte[].class);
            if (svgBytes == null || svgBytes.length == 0) {
                LOG.info("Downloaded crest is empty for teamId: {}. Will use placeholder.", teamId);
                return crestPlaceholderLoader.getCrestPlaceholderIcon();
            }
            return new SvgImage(new String(svgBytes, StandardCharsets.UTF_8));
        } catch (RestClientException e) {
            LOG.info("Could not download crests image for teamId: {}. Cause: {}", teamId, e.getMessage());
            return crestPlaceholderLoader.getCrestPlaceholderIcon();
        }
    }

}
