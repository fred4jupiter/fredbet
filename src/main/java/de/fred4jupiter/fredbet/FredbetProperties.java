package de.fred4jupiter.fredbet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import de.fred4jupiter.fredbet.domain.Country;

/**
 * Contains all configuration properties for FredBet application.
 * 
 * @author michael
 *
 */
@ConfigurationProperties(prefix = "fredbet")
public class FredbetProperties {

	/**
	 * Creates demo data with additional users and matches.
	 */
	private boolean createDemoData;

	private String databaseUrl;

	private String databaseUsername;

	private String databasePassword;

	/**
	 * Adds or removes the navigation entry for demo data creation in
	 * administration menu.
	 */
	private boolean enableDemoDataCreationNavigationEntry;

	public static final Country DEFAULT_FAVOURITE_COUNTRY = Country.GERMANY;

	/**
	 * Sum points per user for selected country that will be shown in points
	 * statistics.
	 */
	private Country favouriteCountry = DEFAULT_FAVOURITE_COUNTRY;

	public static final DatabaseType DEFAULT_DB_TYPE =  DatabaseType.H2;
	
	private DatabaseType databaseType = DEFAULT_DB_TYPE;
	
	/**
	 * Image size of the generated thumbnails.
	 */
	private int thumbnailSize;

	/**
	 * Image size for the uploaded images.
	 */
	private int imageSize;

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public boolean isCreateDemoData() {
		return createDemoData;
	}

	public void setCreateDemoData(boolean createDemoData) {
		this.createDemoData = createDemoData;
	}

	public boolean isEnableDemoDataCreationNavigationEntry() {
		return enableDemoDataCreationNavigationEntry;
	}

	public void setEnableDemoDataCreationNavigationEntry(boolean enableDemoDataCreationNavigationEntry) {
		this.enableDemoDataCreationNavigationEntry = enableDemoDataCreationNavigationEntry;
	}

	public Country getFavouriteCountry() {
		return favouriteCountry;
	}

	public void setFavouriteCountry(Country favouriteCountry) {
		this.favouriteCountry = favouriteCountry;
	}

	public int getThumbnailSize() {
		return thumbnailSize;
	}

	public void setThumbnailSize(int thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	public int getImageSize() {
		return imageSize;
	}

	public void setImageSize(int imageSize) {
		this.imageSize = imageSize;
	}

	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

}
