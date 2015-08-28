package de.fred4jupiter.fredbet.repository;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class UsernamePoints {

	@Id
	private String id;

	private String userName;

	@Field("total")
	private Integer totalPoints;
	
	private String cssRankClass;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("userName", userName);
		builder.append("points", totalPoints);
		return builder.toString();
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCssRankClass() {
		return cssRankClass;
	}

	public void setCssRankClass(String cssRankClass) {
		this.cssRankClass = cssRankClass;
	}

}
