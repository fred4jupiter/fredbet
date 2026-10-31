package de.fred4jupiter.fredbet.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "info_content")
public class InfoContent {

    @Id
    private InfoPK pk;

    @Column(name = "content", nullable = false)
    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    private String content;

    protected InfoContent() {
        // for hibernate
    }

//    public InfoContent(String name, String content, String localeString) {
//        super();
//        this.pk = new InfoPK(name, localeString);
//        this.content = content;
//    }

    public InfoContent(InfoPK pk, String content) {
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
        InfoContent other = (InfoContent) obj;
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
