package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.domain.SvgImage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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
            String svgContent = crestsRestClient.get().uri("/%s.svg".formatted(teamId)).retrieve().body(String.class);
            return new SvgImage(svgContent);
        } catch (HttpClientErrorException e) {
            LOG.info("Could not download crests image flag for teamId: {}. Cause: {}", teamId, e.getMessage());
            return crestPlaceholderLoader.getCrestPlaceholderIcon();
        }
    }

}
