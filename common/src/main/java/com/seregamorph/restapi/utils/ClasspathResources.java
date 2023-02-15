package com.seregamorph.restapi.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class ClasspathResources {

    public static String readString(String resource) {
        return new String(readBytes(resource), UTF_8);
    }

    private static byte[] readBytes(String resource) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return readBytes(classLoader, resource);
    }

    private static byte[] readBytes(ClassLoader classLoader, String resource) {
        Objects.requireNonNull(resource, "resource");
        URL url = classLoader.getResource(resource);

        if (url == null) {
            throw new IllegalStateException(String.format("Missing resource [%s]", resource));
        }

        try (InputStream in = url.openStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error while reading resource [%s]", resource), e);
        }
    }
}
