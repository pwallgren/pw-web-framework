package com.petwal.pwweb.model;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public class RoutePattern {

    private static final String SLASH = "/";

    private final String originalPattern;
    private final Pattern regexPattern;
    private final List<String> variableNames;

    public String getOriginalPattern() {
        return originalPattern;
    }

    public RoutePattern(final String originalPattern) {
        this.originalPattern = originalPattern;
        this.variableNames = new ArrayList<>();

        final String regex = Arrays.stream(originalPattern.split(SLASH))
                .filter(part -> !part.isEmpty())
                .map(this::toPattern)
                .collect(joining(SLASH));
        this.regexPattern = Pattern.compile(SLASH + regex);
    }

    public boolean match(final String path) {
        return regexPattern.matcher(path).matches();
    }

    public Map<String, String> getParamMappings(final String path) {
        final Matcher matcher = regexPattern.matcher(path);
        if (!matcher.matches()) {
            return null;
        }
        final Map<String, String> variables = new HashMap<>();
        for (int i = 0; i < variableNames.size(); i++) {
            variables.put(variableNames.get(i), matcher.group(i + 1));
        }

        return variables;
    }

    private String toPattern(final String pathPart) {
        if (pathPart.startsWith("{") && pathPart.endsWith("}")) {
            final String variableName = pathPart.substring(1, pathPart.length() - 1);
            variableNames.add(variableName);
            return "([^/]+)";
        } else {
            return Pattern.quote(pathPart);
        }
    }
}
