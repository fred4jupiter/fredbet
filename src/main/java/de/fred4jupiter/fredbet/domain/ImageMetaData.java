package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "IMAGE_METADATA")
public class ImageMetaData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMAGE_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "IMAGE_GROUP_ID")
	private ImageGroup imageGroup;

	@Column(name = "IMAGE_KEY", unique = true)
	private String imageKey;

	@Column(name = "DESCRIPTION")
	private String description;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private AppUser owner;

	protected ImageMetaData() {
		// for hibernate
	}

	public ImageMetaData(String imageKey, ImageGroup imageGroup, AppUser owner) {
		this.imageKey = imageKey;
		this.imageGroup = imageGroup;
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public Integer getVersion() {
		return version;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("imageGroup", imageGroup);
		builder.append("imageKey", imageKey);
		builder.append("description", description);
		builder.append("version", version);
		builder.append("owner", owner);
		return builder.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageGroup getImageGroup() {
		return imageGroup;
	}

	public void setImageGroup(ImageGroup imageGroup) {
		this.imageGroup = imageGroup;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public AppUser getOwner() {
		return owner;
	}

	public void setOwner(AppUser owner) {
		this.owner = owner;
	}
}
