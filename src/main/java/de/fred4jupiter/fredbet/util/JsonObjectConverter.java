package de.fred4jupiter.fredbet.util;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class JsonObjectConverter {

    private final ObjectMapper objectMapper;

    public JsonObjectConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object instance) {
        return objectMapper.writeValueAsString(instance);
    }

    public <T> T fromJson(String json, Class<T> classType) {
        return objectMapper.readValue(json, classType);
    }
}
