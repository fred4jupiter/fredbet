package de.fred4jupiter.fredbet.domain;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record SvgImage(byte[] svgBinary, Integer version) {

    public SvgImage(String svgContent) {
        this(svgContent, 0);
    }

    public SvgImage(String svgContent, Integer version) {
        this(svgContent.getBytes(StandardCharsets.UTF_8), version);
    }

    public String svgContent() {
        return new String(svgBinary, StandardCharsets.UTF_8);
    }

    public String getAsBase64() {
        if (svgBinary == null || svgBinary.length == 0) {
            return null;
        }

        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(svgBinary);
    }
}
