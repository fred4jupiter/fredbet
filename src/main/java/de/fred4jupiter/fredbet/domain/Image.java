package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
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
	
	@Column(name = "GALLERY_GROUP")
	private String galleryGroup;
	
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
	
	public Image(String fileName, byte[] imageBinary,  String galleryGroup, byte[] thumbImageBinary) {
		this.fileName = fileName;
		this.imageBinary = imageBinary;
		this.galleryGroup = galleryGroup;
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

	public void setVersion(Integer version) {
		this.version = version;
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
		builder.append("galleryGroup", galleryGroup);
		return builder.toString();
	}

	public String getGalleryGroup() {
		return galleryGroup;
	}

	public void setGalleryGroup(String galleryGroup) {
		this.galleryGroup = galleryGroup;
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
}
