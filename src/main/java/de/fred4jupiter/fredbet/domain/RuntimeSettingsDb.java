package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "RUNTIME_SETTING")
public class RuntimeSettingsDb {

	@Id
	@Column(name = "CONFIG_ID")
	private Long configId;

	@Column(name = "JSON_CONFIG")
	private String jsonConfig;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	protected RuntimeSettingsDb() {
		// for hibernate
	}

	public RuntimeSettingsDb(Long configId) {
		this.configId = configId;
	}

	public Long getConfigId() {
		return configId;
	}

	public String getJsonConfig() {
		return jsonConfig;
	}

	public void setJsonConfig(String jsonConfig) {
		this.jsonConfig = jsonConfig;
	}

	public Integer getVersion() {
		return version;
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
		RuntimeSettingsDb runtimeSettingsDb = (RuntimeSettingsDb) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(configId, runtimeSettingsDb.configId);
		builder.append(jsonConfig, runtimeSettingsDb.jsonConfig);
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(configId);
		builder.append(jsonConfig);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("configId", configId);
		builder.append("jsonConfig", jsonConfig);
		return builder.toString();
	}
}
