package de.fred4jupiter.fredbet.image;

import java.util.Base64;

public record BinaryImage(String key, byte[] imageBinary) {

    public String getAsBase64() {
        if (imageBinary == null) {
            return "";
        }

        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBinary);
    }
}
