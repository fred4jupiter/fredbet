package de.fred4jupiter.fredbet.crests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CrestPlaceholderLoader {

    private static final Logger LOG = LoggerFactory.getLogger(CrestPlaceholderLoader.class);

    private final byte[] crestPlaceholderIcon;

    public CrestPlaceholderLoader(@Value("classpath:static/crests/crests-placeholder.svg") Resource crestPlaceholderResource) {
        this.crestPlaceholderIcon = toByteArray(crestPlaceholderResource);
    }

    public byte[] getCrestPlaceholderIcon() {
        return crestPlaceholderIcon;
    }

    private byte[] toByteArray(Resource resource) {
        try {
            return resource.getContentAsByteArray();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
