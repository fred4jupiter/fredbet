package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Team {

	@Id
	private String id;

	private Country country;

	private String name;

	public Team() {
	}

	public Team(String name) {
		this.name = name;
	}

	public Team(Country country) {
		this.country = country;
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return country != null ? country.name() : name;
	}

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
		Team team = (Team) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, team.id);
		builder.append(country, team.country);
		builder.append(name, team.name);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(country);
		builder.append(name);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("country", country);
		builder.append("name", name);
		return builder.toString();
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

}
