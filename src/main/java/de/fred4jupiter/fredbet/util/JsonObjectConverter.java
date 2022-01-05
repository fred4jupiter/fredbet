package de.fred4jupiter.fredbet.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonObjectConverter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonObjectConverter.class);

    private final ObjectMapper objectMapper;

    public JsonObjectConverter() {
        this.objectMapper = createObjectMapper();
    }

    public JsonObjectConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        // map fields only
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper;
    }

    public String toJson(Object instance) {
        try {
            return objectMapper.writeValueAsString(instance);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            throw new JsonConversionException(e.getMessage(), e);
        }
    }

    public <T> T fromJson(String json, Class<T> classType) {
        try {
            return objectMapper.readValue(json, classType);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new JsonConversionException(e.getMessage(), e);
        }
    }

}
