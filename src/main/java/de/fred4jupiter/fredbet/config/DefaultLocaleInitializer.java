package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DefaultLocaleInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLocaleInitializer.class);

    private final FredbetProperties fredbetProperties;

    @Lazy
    public DefaultLocaleInitializer(FredbetProperties fredbetProperties) {
        this.fredbetProperties = fredbetProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Locale.setDefault(fredbetProperties.getDefaultLocale());
        LOG.info("Setting default locale to: {}", fredbetProperties.getDefaultLocale());
    }
}
