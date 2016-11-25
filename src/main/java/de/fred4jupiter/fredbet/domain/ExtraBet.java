package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "EXTRA_BET")
public class ExtraBet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EXTRA_BET_ID")
	private Long id;
	
	@Column(name = "USER_NAME")
	private String userName;

	@Enumerated(EnumType.STRING)
	@Column(name = "FINAL_WINNER")
	private Country finalWinner;

	@Enumerated(EnumType.STRING)
	@Column(name = "SEMI_FINAL_WINNER")
	private Country semiFinalWinner;

	private Integer points;
	
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
		ExtraBet extraBet = (ExtraBet) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, extraBet.id);
		builder.append(userName, extraBet.userName);
		builder.append(finalWinner, extraBet.finalWinner);
		builder.append(semiFinalWinner, extraBet.semiFinalWinner);
		builder.append(points, extraBet.points);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(userName);
		builder.append(finalWinner);
		builder.append(semiFinalWinner);
		builder.append(points);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("userName", userName);
		builder.append("finalWinner", finalWinner);
		builder.append("semiFinalWinner", semiFinalWinner);
		builder.append("points", points);
		return builder.toString();
	}
	
	public Country getFinalWinner() {
		return finalWinner;
	}

	public void setFinalWinner(Country finalWinner) {
		this.finalWinner = finalWinner;
	}

	public Country getSemiFinalWinner() {
		return semiFinalWinner;
	}

	public void setSemiFinalWinner(Country semiFinalWinner) {
		this.semiFinalWinner = semiFinalWinner;
	}

	public Long getId() {
		return id;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
