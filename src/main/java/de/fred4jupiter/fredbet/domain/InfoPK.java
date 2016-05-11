package de.fred4jupiter.fredbet.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Embeddable
public class InfoPK implements Serializable {

	private static final long serialVersionUID = -287496571451151546L;

	@Column(name = "INFO_NAME")
	private String name;

	@Column(name = "LOCALE_STRING")
	private String localeString;

	protected InfoPK() {
		// for hibernate
	}

	public InfoPK(String name, String localeString) {
		this.name = name;
		this.localeString = localeString;
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
		InfoPK other = (InfoPK) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(name, other.name);
		builder.append(localeString, other.localeString);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(name);
		builder.append(localeString);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("name", name);
		builder.append("localeString", localeString);
		return builder.toString();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocaleString() {
		return localeString;
	}

	public void setLocaleString(String localeString) {
		this.localeString = localeString;
	}
}
