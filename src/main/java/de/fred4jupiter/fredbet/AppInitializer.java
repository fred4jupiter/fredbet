package de.fred4jupiter.fredbet;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Will be used to activate additional profiles in dev profile.
 * 
 * @author MIS
 *
 */
public class AppInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger LOG = LoggerFactory.getLogger(AppInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		final ConfigurableEnvironment environment = applicationContext.getEnvironment();

		LOG.info("Active profiles: {}", Arrays.asList(environment.getActiveProfiles()));
	}

}
