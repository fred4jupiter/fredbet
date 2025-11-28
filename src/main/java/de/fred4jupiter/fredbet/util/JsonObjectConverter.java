package de.fred4jupiter.fredbet.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class JsonObjectConverter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonObjectConverter.class);

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
