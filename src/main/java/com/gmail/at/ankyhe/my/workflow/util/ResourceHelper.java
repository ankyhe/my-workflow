package com.gmail.at.ankyhe.my.workflow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;

public final class ResourceHelper {

    private ResourceHelper() {}

    public static String stringFromResourceFile(final String resourceFileRelativePath) {
        try {
            return doStringFromResourceFile(resourceFileRelativePath);
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static String doStringFromResourceFile(final String resourceFileRelativePath) throws IOException {
        final ClassPathResource resource = new ClassPathResource(resourceFileRelativePath);
        final InputStream inputStream = resource.getInputStream();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
