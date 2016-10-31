package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
	@GeneratedValue
	@Column(name = "IMAGE_ID")
	private Long id;

	@Column(name = "FILE_NAME")
	private String fileName;

	@ManyToOne
	@JoinColumn(name = "IMAGE_GROUP_ID")
	private ImageGroup imageGroup;

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

	public Image(String fileName, byte[] imageBinary, ImageGroup imageGroup, byte[] thumbImageBinary) {
		this.fileName = fileName;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("fileName", fileName);
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
