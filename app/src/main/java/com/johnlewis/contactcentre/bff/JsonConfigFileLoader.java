package com.johnlewis.contactcentre.bff;

import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class JsonConfigFileLoader {

    @SneakyThrows(IOException.class)
    public JsonObject load(String resourcePath) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        return new JsonObject(IOUtils.toString(stream, "UTF-8"));
    }

    public JsonObject loadAllPresent(String... resourcePaths) {
        JsonObject merged = new JsonObject();

        Arrays.stream(resourcePaths).forEach(path -> {
            if (getClass().getClassLoader().getResource(path) != null) {
                JsonObject json = load(path);
                merged.mergeIn(json);
            }
        });

        return merged;
    }
}
