package de.fred4jupiter.fredbet.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "IMAGE_STORE")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMAGE_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "IMAGE_GROUP_ID")
	private ImageGroup imageGroup;

	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = "IMAGE_BINARY")
	private byte[] imageBinary;

	@Lob
	@Column(name = "THUMB_IMAGE_BINARY")
	private byte[] thumbImageBinary;

	@Column(name = "DESCRIPTION")
	private String description;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	protected Image() {
		// for hibernate
	}

	public Image(byte[] imageBinary, ImageGroup imageGroup, byte[] thumbImageBinary) {
		this.imageBinary = imageBinary;
		this.imageGroup = imageGroup;
		this.thumbImageBinary = thumbImageBinary;
	}

	public byte[] getImageBinary() {
		return imageBinary;
	}

	public void setImageBinary(byte[] imageBinary) {
		this.imageBinary = imageBinary;
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
		return builder.toString();
	}

	public byte[] getThumbImageBinary() {
		return thumbImageBinary;
	}

	public void setThumbImageBinary(byte[] thumbImageBinary) {
		this.thumbImageBinary = thumbImageBinary;
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
}
