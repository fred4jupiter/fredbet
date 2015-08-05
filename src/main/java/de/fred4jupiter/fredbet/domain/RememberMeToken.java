package de.fred4jupiter.fredbet.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class RememberMeToken {

	@Id
	private String id;

	private String username;
	private String series;
	private String tokenValue;
	private Date lastUsed;

	public RememberMeToken(String username, String series, String tokenValue, Date lastUsed) {
		this.username = username;
		this.series = series;
		this.tokenValue = tokenValue;
		this.lastUsed = lastUsed;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	
}
