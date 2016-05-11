package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "INFO_CONTENT")
public class Info {

	@Id
	private InfoPK pk;

	@Lob
	@Column(name = "CONTENT")
	private String content;

	protected Info() {
		// for hibernate
	}

	public Info(String name, String content, String localeString) {
		super();
		this.pk = new InfoPK(name, localeString);
		this.content = content;
	}

	public Info(InfoPK pk, String content) {
		super();
		this.pk = pk;
		this.content = content;
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
		Info other = (Info) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(pk, other.pk);
		builder.append(content, other.content);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(pk);
		builder.append(content);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("pk", pk);
		builder.append("content", content);
		return builder.toString();
	}

	public InfoPK getPk() {
		return pk;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
