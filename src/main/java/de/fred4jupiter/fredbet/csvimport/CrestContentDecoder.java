package de.fred4jupiter.fredbet.csvimport;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decodes the crest image given in a CSV column. As a CSV file can only contain text, the crest can be given as
 * <ul>
 *     <li>SVG markup, e.g. {@code <svg ...>...</svg>}</li>
 *     <li>Base64 data URI, e.g. {@code data:image/png;base64,iVBORw0...}</li>
 *     <li>plain Base64 encoded SVG, JPEG or PNG image</li>
 * </ul>
 * The image format itself is checked when the decoded image is converted to a crest.
 */
final class CrestContentDecoder {

    private static final Pattern DATA_URI = Pattern.compile("^data:([^;,]*)(;[^,]*)?,(.*)$", Pattern.DOTALL);

    private CrestContentDecoder() {
    }

    /**
     * @return the decoded image or empty, if the content is neither SVG markup nor valid Base64
     */
    static Optional<byte[]> decode(String content) {
        if (StringUtils.isBlank(content)) {
            return Optional.empty();
        }

        String trimmed = content.strip();
        if (trimmed.startsWith("<")) {
            return Optional.of(trimmed.getBytes(StandardCharsets.UTF_8));
        }

        Matcher matcher = DATA_URI.matcher(trimmed);
        if (matcher.matches()) {
            String parameters = StringUtils.defaultString(matcher.group(2)).toLowerCase(Locale.ROOT);
            if (!parameters.endsWith(";base64")) {
                return Optional.empty();
            }
            return decodeBase64(matcher.group(3));
        }

        return decodeBase64(trimmed);
    }

    private static Optional<byte[]> decodeBase64(String base64) {
        try {
            // line breaks are allowed within the Base64 content, all other characters must be valid
            byte[] decoded = Base64.getDecoder().decode(StringUtils.deleteWhitespace(base64));
            return decoded.length > 0 ? Optional.of(decoded) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
