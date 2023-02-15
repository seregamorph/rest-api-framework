package com.seregamorph.restapi.test.mocks;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.test.TestContext;
import com.seregamorph.restapi.test.TestDescription;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class MockJsonDataStore<F extends MockJsonResponseSetup<T>, T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final List<MockJsonResponseSetup<T>> jsonSetupList;
    private final Class<F> jsonFormatClass;

    public MockJsonDataStore(Class<F> jsonFormatClass, String resourcePath) {
        this.jsonFormatClass = jsonFormatClass;
        this.jsonSetupList = new ArrayList<>();
        readJson(resourcePath);
    }

    @Nonnull
    public T getJsonResponse() {
        return findJsonResponse()
                .orElseThrow(() -> new IllegalStateException("Could not find matching mock response for current "
                        + TestContext.getCurrentTest()));
    }

    public Optional<T> findJsonResponse() {
        TestDescription description = TestContext.getCurrentTest();

        if (description == null) {
            throw new IllegalStateException("TestContext is not initialized");
        }

        Optional<T> responseOptional = findJsonResponse(description);

        if (responseOptional.isPresent()) {
            log.debug("Found mock json response for ({})", description);
            return responseOptional;
        }

        return Optional.empty();
    }

    @SuppressWarnings("unused")
    protected void validate(T response) {
        // Do nothing by default
    }

    private Optional<T> findJsonResponse(TestDescription description) {
        List<MockJsonResponseSetup<T>> setups = jsonSetupList.stream()
                .filter(setup -> setup.getMatchers().stream()
                        .anyMatch(matcher -> matcher.matches(description)))
                .collect(toList());

        if (setups.size() > 1) {
            throw new IllegalStateException(
                    String.format("Found %d mock JSON response setups for (%s).", setups.size(), description));
        }

        return setups.isEmpty() ? Optional.empty() : Optional.of(setups.get(0).getResponse());
    }

    private void readJson(String resourcePath) {
        URL resource = getClass().getClassLoader()
                .getResource(resourcePath);

        if (resource == null) {
            throw new IllegalArgumentException("Path is not accessible: " + resourcePath);
        }

        readJson(new File(resource.getPath()));
    }

    private void readJson(File file) {
        if (file.isDirectory()) {
            readJsonDir(file);
        } else if (file.getName().endsWith(".json")) {
            readJsonFile(file);
        } else {
            log.info("Skip {}", file.getAbsolutePath());
        }
    }

    private void readJsonDir(File dirFile) {
        File[] innerFiles = dirFile.listFiles();

        if (innerFiles != null) {
            for (File innerFile : innerFiles) {
                readJson(innerFile);
            }
        }
    }

    private void readJsonFile(File file) {
        log.info("Reading mock json data from {}...", file.getAbsolutePath());
        F setup;

        try {
            setup = OBJECT_MAPPER.readValue(file, jsonFormatClass);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read json from " + file.getAbsolutePath()
                    + " as " + jsonFormatClass.getName(), e);
        }

        for (MockJsonMatcher matcher : setup.getMatchers()) {
            val errors = new ArrayList<String>();
            matcher.validate(errors);
            if (!errors.isEmpty()) {
                throw new IllegalStateException("Invalid matcher from file " + file.getPath()
                        + " " + matcher + ": " + errors);
            }
        }

        log.info("Found json setup for " + toString(setup.getMatchers()));

        validate(setup.getResponse());
        jsonSetupList.add(setup);
    }

    private static String toString(List<MockJsonMatcher> matchers) {
        List<String> strings = new ArrayList<>();

        for (MockJsonMatcher matcher : matchers) {
            strings.add(matcher.toString());
        }

        return "[" + String.join(", ", strings) + "]";
    }
}
