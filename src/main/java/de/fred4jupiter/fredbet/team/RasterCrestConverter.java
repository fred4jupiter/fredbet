package de.fred4jupiter.fredbet.team;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

/**
 * Wraps an uploaded JPEG or PNG crest into an SVG document, so that all crests can be stored and served as SVG.
 *
 * <p>The image is re-encoded in its original format (which removes all metadata like GPS positions and keeps the
 * transparency of PNG images) and scaled down to {@link #MAX_CREST_SIZE}, because crests are only displayed in
 * small sizes. The image is not vectorized, it is embedded as data URI.
 */
class RasterCrestConverter {

    static final int MAX_CREST_SIZE = 512;

    private static final String SVG_TEMPLATE = """
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 %1$d %2$d" width="%1$d" height="%2$d">\
        <image width="%1$d" height="%2$d" href="data:%3$s;base64,%4$s"/></svg>""";

    enum RasterFormat {
        JPEG("jpg", "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}),
        PNG("png", "image/png", new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});

        private final String formatName;

        private final String mimeType;

        private final byte[] signature;

        RasterFormat(String formatName, String mimeType, byte[] signature) {
            this.formatName = formatName;
            this.mimeType = mimeType;
            this.signature = signature;
        }

        private boolean matches(byte[] content) {
            return content.length > signature.length && Arrays.equals(content, 0, signature.length, signature, 0, signature.length);
        }
    }

    private RasterCrestConverter() {
    }

    static Optional<RasterFormat> detectFormat(byte[] content) {
        if (content == null) {
            return Optional.empty();
        }
        return Arrays.stream(RasterFormat.values()).filter(format -> format.matches(content)).findFirst();
    }

    static boolean isRasterImage(byte[] content) {
        return detectFormat(content).isPresent();
    }

    /**
     * @throws InvalidCrestException if the given content is no readable JPEG or PNG image
     */
    static String toSvg(byte[] content) {
        RasterFormat format = detectFormat(content)
            .orElseThrow(() -> new InvalidCrestException("Given crest is no JPEG or PNG image!"));

        try {
            // reading from the stream respects the EXIF orientation of photos
            BufferedImage image = Thumbnails.of(new ByteArrayInputStream(content)).scale(1.0).asBufferedImage();
            if (image.getWidth() > MAX_CREST_SIZE || image.getHeight() > MAX_CREST_SIZE) {
                image = Thumbnails.of(image).size(MAX_CREST_SIZE, MAX_CREST_SIZE).asBufferedImage();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(image).scale(1.0).outputFormat(format.formatName);
            if (format == RasterFormat.JPEG) {
                builder.outputQuality(0.9);
            }
            builder.toOutputStream(out);

            String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
            return SVG_TEMPLATE.formatted(image.getWidth(), image.getHeight(), format.mimeType, base64);
        } catch (IOException | IllegalArgumentException e) {
            throw new InvalidCrestException("Given crest is no valid " + format + " image: " + e.getMessage());
        }
    }
}
