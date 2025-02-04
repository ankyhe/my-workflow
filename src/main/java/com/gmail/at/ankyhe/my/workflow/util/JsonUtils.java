package com.gmail.at.ankyhe.my.workflow.util;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {}

    public static String toString(final Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> T fromJsonString(final String str, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (final JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> T fromJsonString(final String str, final TypeReference<T> ref) {
        try {
            return OBJECT_MAPPER.readValue(str, ref);
        } catch (final JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
