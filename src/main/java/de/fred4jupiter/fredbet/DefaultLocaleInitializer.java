package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DefaultLocaleInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLocaleInitializer.class);

    public DefaultLocaleInitializer(FredbetProperties fredbetProperties) {
        Locale.setDefault(fredbetProperties.getDefaultLocale());
        LOG.info("Setting default locale to: {}", fredbetProperties.getDefaultLocale());
    }
}
