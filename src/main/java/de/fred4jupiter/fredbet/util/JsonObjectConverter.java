package de.fred4jupiter.fredbet.util;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@Component
public class JsonObjectConverter {

    private final Gson gson = new Gson();

    public <T> T fromJson(String json, Class<T> targetType) {
        return gson.fromJson(json, targetType);
    }

    public <T> String toJson(T instance) {
        return gson.toJson(instance);
    }
}
