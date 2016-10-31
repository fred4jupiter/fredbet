package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "IMAGE_GROUP")
public class ImageGroup {

	@Id
	@GeneratedValue
	@Column(name = "IMAGE_GROUP_ID")
	private Long id;

	@Column(name = "NAME", unique = true)
	private String name;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	protected ImageGroup() {
		// for hibernate
	}
	
	public ImageGroup(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("id", id);
		builder.append("name", name);
		return builder.toString();
	}
}
