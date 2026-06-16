package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.common.UnitTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class SvgImageUT {

    @Test
    public void getAsBase64UsesSvgMimeType() {
        SvgImage svgImage = new SvgImage("<svg xmlns='http://www.w3.org/2000/svg'></svg>");

        assertThat(svgImage.getAsBase64()).startsWith("data:image/svg+xml;base64,");
    }
}

