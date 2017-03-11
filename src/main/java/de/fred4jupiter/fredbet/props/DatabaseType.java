package de.fred4jupiter.fredbet.props;

/**
 * The configured database type to be used.
 * 
 * @author michael
 *
 */
public enum DatabaseType {

	H2("org.h2.Driver", "org.hibernate.dialect.H2Dialect"),

	MYSQL("com.mysql.jdbc.Driver", "org.hibernate.dialect.MySQLDialect"),

	MARIA_DB("org.mariadb.jdbc.Driver", "org.hibernate.dialect.MySQLDialect"),

	POSTGRES("org.postgresql.Driver", "org.hibernate.dialect.PostgreSQL9Dialect");

	private final String driverClassName;
	
	private final String databasePlatform;

	private DatabaseType(String driverClassName, String databasePlatform) {
		this.driverClassName = driverClassName;
		this.databasePlatform = databasePlatform;
	}

	public String getDriverClassName() {
		return driverClassName;
	}
	
	public String getDatabasePlatform() {
		return databasePlatform;
	}
}
