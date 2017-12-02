package de.fred4jupiter.fredbet.props;

/**
 * All available spring profiles within this application.
 * 
 * @author michael
 *
 */
public interface FredBetProfile {

	String DEV = "dev";
	
	String PROD = "prod";

	String LOCALDB = "localdb";

	String INTEGRATION_TEST = "integration_test";
}
