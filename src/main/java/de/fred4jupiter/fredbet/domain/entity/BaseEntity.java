package de.fred4jupiter.fredbet.domain.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public abstract class BaseEntity {

    @CreatedDate
    protected LocalDateTime createDate;

    @LastModifiedDate
    protected LocalDateTime updateDate;

    @CreatedBy
    protected String createdBy;

    @LastModifiedBy
    protected String updatedBy;

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
