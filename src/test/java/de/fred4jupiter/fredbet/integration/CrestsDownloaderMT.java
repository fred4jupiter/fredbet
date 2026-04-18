package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class CrestsDownloaderMT {

    private static final Logger LOG = LoggerFactory.getLogger(CrestsDownloaderMT.class);

    @Autowired
    private CrestsDownloader crestsDownloader;

    @Test
    void download() {
        Optional<byte[]> imageOpt = crestsDownloader.downloadCrestsByUrl("759");
        assertThat(imageOpt.isPresent());

        byte[] byteArray = imageOpt.get();
        TempFileWriterUtil.writeToTempFolder(byteArray, "test.svg");

        String base64Image = "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(byteArray);
        LOG.debug("base64Image: {}", base64Image);
    }
}
