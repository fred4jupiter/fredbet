package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class CrestsDownloaderMT {

    private static final Logger LOG = LoggerFactory.getLogger(CrestsDownloaderMT.class);

    @Autowired
    private CrestsDownloader crestsDownloader;

    @Test
    void download() {
        SvgImage svgImage = crestsDownloader.downloadCrestsByUrl("759");
        assertThat(svgImage).isNotNull();
        assertThat(svgImage.svgContent()).isNotBlank();
        assertThat(svgImage.getAsByteArray()).isNotNull();

        TempFileWriterUtil.writeToTempFolder(svgImage.getAsByteArray(), "test.svg");

        String base64Image = svgImage.getAsBase64();
        assertThat(base64Image).isNotBlank();
        LOG.debug("base64Image: {}", base64Image);
    }
}
