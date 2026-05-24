package de.fred4jupiter.fredbet.settings;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class RuntimeSettingsRepositoryIT {

    @Autowired
    private RuntimeSettingsRepository runtimeSettingsRepository;

    @Autowired
    private RuntimeSettingsDbRepository runtimeSettingsDbRepository;

    @Test
    public void loadRuntimeSettings_withSerializableJson_returnsObject() {
        Long id = 100L;
        RuntimeSettingsDb db = new RuntimeSettingsDb(id);
        db.setJsonConfig("{\"value\":\"hello-integration\"}");

        runtimeSettingsDbRepository.save(db);

        ExampleSettings loaded = runtimeSettingsRepository.loadRuntimeSettings(id, ExampleSettings.class);

        assertThat(loaded).isNotNull();
        assertThat(loaded.getValue()).isEqualTo("hello-integration");
    }

    @Test
    public void loadRuntimeSettings_withNonSerializableJson_returnsNull() {
        Long id = 101L;
        RuntimeSettingsDb db = new RuntimeSettingsDb(id);
        db.setJsonConfig("not-a-json-value");

        runtimeSettingsDbRepository.save(db);

        ExampleSettings loaded = runtimeSettingsRepository.loadRuntimeSettings(id, ExampleSettings.class);

        assertThat(loaded).isNull();
    }

    // simple POJO for testing Jackson (must be public static for Jackson in some environments)
    public static class ExampleSettings {
        private String value;

        public ExampleSettings() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

