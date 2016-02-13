package de.fred4jupiter.fredbet.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "REMEMBERME_TOKEN")
public class RememberMeToken {

	@Id
	@GeneratedValue
	private Long id;

	private String username;
	private String series;
	private String tokenValue;
	private Date lastUsed;
	
	protected RememberMeToken() {
		// for hibernate
	}

	public RememberMeToken(String username, String series, String tokenValue, Date lastUsed) {
		this.username = username;
		this.series = series;
		this.tokenValue = tokenValue;
		this.lastUsed = lastUsed;
	}

	public Long getId() {
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
