package de.fred4jupiter.fredbet;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.props.FredbetProperties;

@Component
public class DefaultLocaleInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultLocaleInitializer.class);

	@Autowired
	public DefaultLocaleInitializer(FredbetProperties fredbetProperties) {
		Locale.setDefault(fredbetProperties.getDefaultLocale());
		LOG.info("Setting default locale to: {}", fredbetProperties.getDefaultLocale());
	}

}
