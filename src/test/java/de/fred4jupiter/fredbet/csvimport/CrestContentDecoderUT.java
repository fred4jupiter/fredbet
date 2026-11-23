package de.fred4jupiter.fredbet.csvimport;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class CrestContentDecoderUT {

    private static final String SVG = "<svg xmlns=\"http://www.w3.org/2000/svg\"/>";

    private static final byte[] SVG_BYTES = SVG.getBytes(StandardCharsets.UTF_8);

    @Test
    public void svgMarkup() {
        assertThat(CrestContentDecoder.decode("  " + SVG + "\n")).contains(SVG_BYTES);
    }

    @Test
    public void base64DataUri() {
        String base64 = Base64.getEncoder().encodeToString(SVG_BYTES);

        assertThat(CrestContentDecoder.decode("data:image/svg+xml;base64," + base64)).contains(SVG_BYTES);
        assertThat(CrestContentDecoder.decode("data:image/png;BASE64," + base64)).contains(SVG_BYTES);
    }

    @Test
    public void plainBase64WithLineBreaks() {
        String base64 = Base64.getMimeEncoder(8, "\r\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(SVG_BYTES);

        assertThat(CrestContentDecoder.decode(base64)).contains(SVG_BYTES);
    }

    @Test
    public void invalidContent() {
        assertThat(CrestContentDecoder.decode(null)).isEmpty();
        assertThat(CrestContentDecoder.decode("  ")).isEmpty();
        assertThat(CrestContentDecoder.decode("no image at all!")).isEmpty();
        assertThat(CrestContentDecoder.decode("data:image/svg+xml;utf8," + SVG)).isEmpty();
        assertThat(CrestContentDecoder.decode("data:image/png;base64,")).isEmpty();
    }
}
