package de.fred4jupiter.fredbet.team;

import de.fred4jupiter.fredbet.team.RasterCrestConverter.RasterFormat;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RasterCrestConverterUT {

    private static final Pattern DATA_URI = Pattern.compile("href=\"data:image/png;base64,([^\"]+)\"");

    @Test
    public void detectFormat() {
        assertThat(RasterCrestConverter.detectFormat(createJpeg(10, 10))).contains(RasterFormat.JPEG);
        assertThat(RasterCrestConverter.detectFormat(createPng(10, 10))).contains(RasterFormat.PNG);
        assertThat(RasterCrestConverter.detectFormat("<svg/>".getBytes(StandardCharsets.UTF_8))).isEmpty();
        assertThat(RasterCrestConverter.detectFormat(new byte[0])).isEmpty();
        assertThat(RasterCrestConverter.detectFormat(null)).isEmpty();
    }

    @Test
    public void smallJpegKeepsItsSize() {
        String svg = RasterCrestConverter.toSvg(createJpeg(100, 80));

        assertThat(svg).contains("viewBox=\"0 0 100 80\"").contains("href=\"data:image/jpeg;base64,");
        assertThat(SvgCrestValidator.isValidSvg(svg.getBytes(StandardCharsets.UTF_8))).isTrue();
    }

    @Test
    public void largeJpegIsScaledDownKeepingAspectRatio() {
        String svg = RasterCrestConverter.toSvg(createJpeg(1024, 512));

        assertThat(svg).contains("viewBox=\"0 0 512 256\"");
    }

    @Test
    public void pngStaysPngAndKeepsTransparency() throws IOException {
        String svg = RasterCrestConverter.toSvg(createPng(600, 1200));

        assertThat(svg).contains("viewBox=\"0 0 256 512\"");
        assertThat(SvgCrestValidator.isValidSvg(svg.getBytes(StandardCharsets.UTF_8))).isTrue();

        Matcher matcher = DATA_URI.matcher(svg);
        assertThat(matcher.find()).isTrue();
        BufferedImage embedded = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(matcher.group(1))));
        assertThat(embedded.getColorModel().hasAlpha()).isTrue();
        assertThat(embedded.getRGB(0, 0) >>> 24).isZero();
    }

    @Test
    public void brokenImagesAreRejected() {
        byte[] brokenJpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00, 0x01, 0x02};
        byte[] brokenPng = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0x00, 0x01};

        assertThatThrownBy(() -> RasterCrestConverter.toSvg(brokenJpeg)).isInstanceOf(InvalidCrestException.class);
        assertThatThrownBy(() -> RasterCrestConverter.toSvg(brokenPng)).isInstanceOf(InvalidCrestException.class);
    }

    @Test
    public void noRasterImageIsRejected() {
        assertThatThrownBy(() -> RasterCrestConverter.toSvg("<svg/>".getBytes(StandardCharsets.UTF_8))).isInstanceOf(InvalidCrestException.class);
    }

    static byte[] createJpeg(int width, int height) {
        return write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "jpg");
    }

    /**
     * Creates a fully transparent PNG image.
     */
    static byte[] createPng(int width, int height) {
        return write(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), "png");
    }

    private static byte[] write(BufferedImage image, String formatName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
