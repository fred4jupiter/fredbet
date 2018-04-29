package de.fred4jupiter.fredbet.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "SESSION_TRACKING")
public class SessionTracking {

	@Id
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "SESSION_ID")
	private String sessionId;
	
	@Column(name = "LAST_LOGIN")
	private LocalDateTime lastLogin;
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		SessionTracking other = (SessionTracking) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(userName, other.userName);
		builder.append(sessionId, other.sessionId);
		builder.append(lastLogin, other.lastLogin);
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(userName);
		builder.append(sessionId);
		builder.append(lastLogin);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("userName", userName);
		builder.append("sessionId", sessionId);
		builder.append("lastLogin", lastLogin);
		return builder.toString();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
