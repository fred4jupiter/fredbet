package de.fred4jupiter.fredbet;

/**
 * All available spring profiles within this application.
 * 
 * @author michael
 *
 */
public interface FredBetProfile {

	String DEV = "dev";
	
	String TEST = "test";
	
	String PROD = "prod";
	
	String DEMODATA = "demodata";
	
	/**
	 * This is the MongoDB in memory usage.
	 */
	String FONGO = "fongo";
}
