package de.fred4jupiter.fredbet.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;

/**
 * Will only be used if the images are stored within the database.
 *
 * @author michael
 *
 */
@Entity
@Table(name = "image_binary")
public class ImageBinary {

    @Id
    @Column(name = "image_key")
    private String key;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_bytes", nullable = false)
    @JdbcTypeCode(SqlTypes.LONG32VARBINARY)
    private byte[] imageBinary;

    @Column(name = "thumb_image_bytes")
    @JdbcTypeCode(SqlTypes.LONG32VARBINARY)
    private byte[] thumbImageBinary;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    protected ImageBinary() {
        // for hibernate
    }

    public ImageBinary(String key, byte[] imageBinary) {
        this.key = key;
        this.imageBinary = imageBinary;
    }

    public ImageBinary(byte[] imageBinary, byte[] thumbImageBinary) {
        this(UUID.randomUUID().toString(), imageBinary, thumbImageBinary);
    }

    public ImageBinary(String key, byte[] imageBinary, byte[] thumbImageBinary) {
        this.key = key;
        this.imageBinary = imageBinary;
        this.thumbImageBinary = thumbImageBinary;
    }

    public String getKey() {
        return key;
    }

    public byte[] getImageBinary() {
        return imageBinary;
    }

    public byte[] getThumbImageBinary() {
        return thumbImageBinary;
    }

    public void setImageBinary(byte[] imageBinary) {
        this.imageBinary = imageBinary;
    }

    public void setThumbImageBinary(byte[] thumbImageBinary) {
        this.thumbImageBinary = thumbImageBinary;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("key", key);
        builder.append("image size", imageBinary != null ? imageBinary.length : 0);
        builder.append("image thumb size", thumbImageBinary != null ? thumbImageBinary.length : 0);
        return builder.toString();
    }

}
