package de.fred4jupiter.fredbet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class AppInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger LOG = LoggerFactory.getLogger(AppInitializer.class);
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		if (environment.acceptsProfiles("dev", "default")) {
			environment.addActiveProfile("demodata");
			environment.addActiveProfile("mongo-embedded");
			LOG.info("added active profile demodata and mongo-embedded");
		}
	}

}
