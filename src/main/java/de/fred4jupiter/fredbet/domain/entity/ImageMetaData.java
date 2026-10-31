package de.fred4jupiter.fredbet.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "image_meta_data")
public class ImageMetaData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "image_group_id", nullable = false)
	private ImageGroup imageGroup;

	@Column(name = "image_key", unique = true, nullable = false)
	private String imageKey;

	@Column(name = "description")
	private String description;

	@Version
	@Column(name = "version", nullable = false)
	private Integer version;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
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
