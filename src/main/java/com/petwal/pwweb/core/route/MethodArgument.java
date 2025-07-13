package com.petwal.pwweb.core.route;

import java.util.Objects;

public class MethodArgument {
    private final String name;
    private final Class<?> type;
    private final boolean isPath;
    private final boolean isQuery;

    private MethodArgument(final String name, final Class<?> type, final boolean isPath, final boolean isQuery) {
        this.name = name;
        this.type = type;
        this.isPath = isPath;
        this.isQuery = isQuery;
    }

    public static MethodArgument path(final String name, final Class<?> type) {
        return new MethodArgument(name, type, true, false);
    }

    public static MethodArgument query(final String name, final Class<?> type) {
        return new MethodArgument(name, type, false, true);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isPath() {
        return isPath;
    }

    public boolean isQuery() {
        return isQuery;
    }

    @Override
    public String toString() {
        return "ParameterMeta{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", isPath=" + isPath +
                ", isQuery=" + isQuery +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final MethodArgument that = (MethodArgument) o;
        return isPath == that.isPath
                && isQuery == that.isQuery
                && Objects.equals(name, that.name)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, isPath, isQuery);
    }
}
