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
@Table(name = "FILE_STORAGE")
public class FileStorage {

	@Id
	@GeneratedValue
	@Column(name = "IMAGE_ID")
	private Long id;
	
	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Lob
	@Column(name = "IMAGE_BINARY")
	private byte[] imageBinary;
	
	@Version
	@Column(name = "VERSION")
	private Integer version;
	
	protected FileStorage() {
		// for hibernate
	}
	
	public FileStorage(String fileName, byte[] imageBinary) {
		this.fileName = fileName;
		this.imageBinary = imageBinary;
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
		return builder.toString();
	}
}
