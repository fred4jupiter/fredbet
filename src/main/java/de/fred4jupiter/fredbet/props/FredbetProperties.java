package de.fred4jupiter.fredbet.props;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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

	public static final DatabaseType DEFAULT_DB_TYPE = DatabaseType.H2;

	private DatabaseType databaseType = DEFAULT_DB_TYPE;

	/**
	 * Image size of the generated thumbnails.
	 */
	private int thumbnailSize;

	/**
	 * Image size for the uploaded images.
	 */
	private int imageSize;

	/**
	 * Selection of possible image storage locations.
	 */
	private ImageLocation imageLocation;

	/**
	 * Path in file system to store images in case of image location is set to
	 * 'file-system'
	 */
	private String imageFileSystemBaseFolder;

	/**
	 * Password used if the user password has been reset.
	 */
	private String passwordForReset;

	/**
	 * The AWS S3 bucket name to use to store images in.
	 */
	private String awsS3bucketName;

	/**
	 * Extra betting points for final winner.
	 */
	private int pointsFinalWinner;

	/**
	 * Extra betting points for semi final winner.
	 */
	private int pointsSemiFinalWinner;

	/**
	 * Extra betting points for third winner.
	 */
	private int pointsThirdFinalWinner;

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

	public ImageLocation getImageLocation() {
		return imageLocation;
	}

	public void setImageLocation(ImageLocation imageLocation) {
		this.imageLocation = imageLocation;
	}

	

	public String getPasswordForReset() {
		return passwordForReset;
	}

	public void setPasswordForReset(String passwordForReset) {
		this.passwordForReset = passwordForReset;
	}

	public String getAwsS3bucketName() {
		return awsS3bucketName;
	}

	public void setAwsS3bucketName(String awsS3bucketName) {
		this.awsS3bucketName = awsS3bucketName;
	}

	public int getPointsFinalWinner() {
		return pointsFinalWinner;
	}

	public void setPointsFinalWinner(int pointsFinalWinner) {
		this.pointsFinalWinner = pointsFinalWinner;
	}

	public int getPointsSemiFinalWinner() {
		return pointsSemiFinalWinner;
	}

	public void setPointsSemiFinalWinner(int pointsSemiFinalWinner) {
		this.pointsSemiFinalWinner = pointsSemiFinalWinner;
	}

	public int getPointsThirdFinalWinner() {
		return pointsThirdFinalWinner;
	}

	public void setPointsThirdFinalWinner(int pointsThirdFinalWinner) {
		this.pointsThirdFinalWinner = pointsThirdFinalWinner;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("createDemoData", createDemoData);
		builder.append("databaseUrl", databaseUrl);
		builder.append("databasePassword", databasePassword != null ? "********" : "null");
		builder.append("enableDemoDataCreationNavigationEntry", enableDemoDataCreationNavigationEntry);
		builder.append("databaseType", databaseType);
		builder.append("thumbnailSize", thumbnailSize);
		builder.append("imageSize", imageSize);
		builder.append("imageLocation", imageLocation);
		
		builder.append("imageFileSystemBaseFolder", imageFileSystemBaseFolder);
		builder.append("passwordForReset", passwordForReset);
		builder.append("awsS3bucketName", awsS3bucketName);
		builder.append("pointsFinalWinner", pointsFinalWinner);
		builder.append("pointsSemiFinalWinner", pointsSemiFinalWinner);
		builder.append("pointsThirdFinalWinner", pointsThirdFinalWinner);
		return builder.toString();
	}

	public String getImageFileSystemBaseFolder() {
		return imageFileSystemBaseFolder;
	}

	public void setImageFileSystemBaseFolder(String imageFileSystemBaseFolder) {
		this.imageFileSystemBaseFolder = imageFileSystemBaseFolder;
	}
}
