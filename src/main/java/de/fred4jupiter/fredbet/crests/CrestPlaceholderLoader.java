package de.fred4jupiter.fredbet.crests;

import de.fred4jupiter.fredbet.domain.SvgImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CrestPlaceholderLoader {

    private static final Logger LOG = LoggerFactory.getLogger(CrestPlaceholderLoader.class);

    private final SvgImage crestPlaceholderIcon;

    public CrestPlaceholderLoader(@Value("classpath:static/crests/crests-placeholder.svg") Resource crestPlaceholderResource) {
        this.crestPlaceholderIcon = toByteArray(crestPlaceholderResource);
    }

    public SvgImage getCrestPlaceholderIcon() {
        return crestPlaceholderIcon;
    }

    private SvgImage toByteArray(Resource resource) {
        try {
            return new SvgImage(resource.getContentAsString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
