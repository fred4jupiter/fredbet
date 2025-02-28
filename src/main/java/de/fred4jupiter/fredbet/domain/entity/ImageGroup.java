package de.fred4jupiter.fredbet.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "IMAGE_GROUP")
public class ImageGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMAGE_GROUP_ID")
	private Long id;

	@Column(name = "NAME", unique = true)
	private String name;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	@Column(name = "USER_PROFILE_IMG_GROUP")
	private boolean userProfileImageGroup;

	protected ImageGroup() {
		// for hibernate
	}

	public ImageGroup(String name) {
		this.name = name;
	}

	public ImageGroup(String name, boolean userProfileImageGroup) {
		this.name = name;
		this.userProfileImageGroup = userProfileImageGroup;
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
		builder.append("userProfileImageGroup", userProfileImageGroup);
		return builder.toString();
	}

	public boolean isUserProfileImageGroup() {
		return userProfileImageGroup;
	}

	public void setUserProfileImageGroup(boolean userProfileImageGroup) {
		this.userProfileImageGroup = userProfileImageGroup;
	}

	public boolean isDefaultImageGroup() {
		return FredbetConstants.DEFAULT_IMAGE_GROUP_NAME.equals(this.name) || FredbetConstants.GALLERY_NAME.equals(this.name);
	}
}
