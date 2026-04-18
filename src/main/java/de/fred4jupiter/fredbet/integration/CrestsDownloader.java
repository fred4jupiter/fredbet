package de.fred4jupiter.fredbet.integration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
class CrestsDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(CrestsDownloader.class);

    private final RestClient crestsRestClient;

    CrestsDownloader(RestClient crestsRestClient) {
        this.crestsRestClient = crestsRestClient;
    }

    public Optional<byte[]> downloadCrestsByUrl(String teamId) {
        if (StringUtils.isBlank(teamId)) {
            LOG.warn("teamId is blank. No crest will be downloaded.");
            return Optional.empty();
        }

        LOG.debug("will try to download crests with teamId: {}", teamId);

        try {
            byte[] imageAsByteArray = crestsRestClient.get().uri("/%s.svg".formatted(teamId)).retrieve().body(byte[].class);
            return Optional.ofNullable(imageAsByteArray);
        } catch (HttpClientErrorException e) {
            LOG.error("Could not download crests image flag for teamId: {}. Cause: {}", teamId, e.getMessage());
            return Optional.empty();
        }
    }

}
