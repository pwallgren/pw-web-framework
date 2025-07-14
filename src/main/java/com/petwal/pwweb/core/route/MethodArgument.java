package com.petwal.pwweb.core.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petwal.pwweb.http.HttpRequest;

import java.util.Objects;

public class MethodArgument {
    private final String name;
    private final Class<?> type;
    private final ObjectMapper objectMapper;
    private final boolean isPath;
    private final boolean isQuery;
    private final boolean isBody;

    private MethodArgument(final String name, final Class<?> type, final boolean isPath, final boolean isQuery, final boolean isBody, final ObjectMapper objectMapper) {
        this.name = name;
        this.type = type;
        this.isPath = isPath;
        this.isQuery = isQuery;
        this.isBody = isBody;
        this.objectMapper = objectMapper;
    }

    private MethodArgument(final String name, final Class<?> type, final boolean isPath, final boolean isQuery, final boolean isBody) {
        this(name, type, isPath, isQuery, isBody, null);
    }

    public static MethodArgument path(final String name, final Class<?> type) {
        return new MethodArgument(name, type, true, false, false);
    }

    public static MethodArgument query(final String name, final Class<?> type) {
        return new MethodArgument(name, type, false, true, false);
    }

    public static MethodArgument body(final Class<?> type, final ObjectMapper objectMapper) {
        return new MethodArgument("body", type, false, false, true, objectMapper);
    }

    public static MethodArgument request() {
        return new MethodArgument("request", HttpRequest.class, false, false, false);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public boolean isPath() {
        return isPath;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public boolean isBody() {
        return isBody;
    }

    public boolean isHttpRequest() {
        return type.equals(HttpRequest.class);
    }

    @Override
    public String toString() {
        return "ParameterMeta{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", isPath=" + isPath +
                ", isQuery=" + isQuery +
                ", isBody=" + isBody +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final MethodArgument that = (MethodArgument) o;
        return isPath == that.isPath
                && isQuery == that.isQuery
                && isBody == that.isBody
                && Objects.equals(name, that.name)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, isPath, isQuery, isBody);
    }
}
