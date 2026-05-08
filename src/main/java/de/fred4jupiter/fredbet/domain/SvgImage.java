package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record SvgImage(String svgContent, Integer version) {

    public SvgImage(String svgContent) {
        this(svgContent, 1);
    }

    public byte[] getAsByteArray() {
        if (svgContent == null) {
            return null;
        }
        return svgContent.getBytes(StandardCharsets.UTF_8);
    }

    public String getAsBase64() {
        if (StringUtils.isBlank(this.svgContent)) {
            return null;
        }

        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(getAsByteArray());
    }
}
