package com.slyak.es.util;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResourceUtils {
    private static final ResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

    public static Resource getResource(String location) {
        return RESOURCE_LOADER.getResource(location);
    }

    @SneakyThrows
    public static InputStream getResourceInputStream(String location) {
        return getResource(location).getInputStream();
    }

    @SneakyThrows
    public static String getResourceString(String location) {
        return IOUtils.toString(getResourceInputStream(location), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static List<String> getResourceLines(String location) {
        return IOUtils.readLines(getResourceInputStream(location), StandardCharsets.UTF_8);
    }

}
