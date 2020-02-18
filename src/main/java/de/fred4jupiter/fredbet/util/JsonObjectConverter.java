package de.fred4jupiter.fredbet.util;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@Component
public class JsonObjectConverter {

    public <T> T fromJson(String json, Class<T> targetType) {
        Gson gson = new Gson();
        return gson.fromJson(json, targetType);
    }

    public <T> String toJson(T instance) {
        Gson gson = new Gson();
        return gson.toJson(instance);
    }
}
