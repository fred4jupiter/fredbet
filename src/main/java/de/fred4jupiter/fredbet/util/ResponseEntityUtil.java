package de.fred4jupiter.fredbet.util;

import org.springframework.http.ResponseEntity;

public final class ResponseEntityUtil {

    private ResponseEntityUtil() {
        // only static methods
    }

    public static ResponseEntity<byte[]> createResponseEntity(String fileName, byte[] fileContent, String contentType) {
        return createResponseEntity(fileName, fileContent, contentType, DownloadType.ATTACHMENT);
    }

    public static ResponseEntity<byte[]> createResponseEntity(String fileName, byte[] fileContent, String contentType, DownloadType downloadType) {
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().header("Content-Type", contentType)
                .header("Content-Disposition", downloadType.getHeaderValue() + "; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    public enum DownloadType {
        INLINE("inline"),

        ATTACHMENT("attachment");

        private final String headerValue;

        DownloadType(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getHeaderValue() {
            return headerValue;
        }
    }
}
