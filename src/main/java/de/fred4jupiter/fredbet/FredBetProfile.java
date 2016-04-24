package de.fred4jupiter.fredbet;

/**
 * All available spring profiles within this application.
 * 
 * @author michael
 *
 */
public interface FredBetProfile {

	String DEV = "dev";

	String DOCKER = "docker";

	String DEMODATA = "demodata";
	
	String LOCALDB = "localdb";

	String INTEGRATION_TEST = "integration_test";
}
