package com.petwal.pwweb.core.route;

import com.petwal.pwweb.http.HttpRequest;
import com.petwal.pwweb.http.HttpResponse;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class HandlerMethod {
    private final Object instance;
    private final Method method;
    private final List<MethodArgument> methodArguments;

    private HandlerMethod(final Object instance, final Method method, final List<MethodArgument> methodArguments) {
        this.instance = instance;
        this.method = method;
        this.methodArguments = methodArguments;
    }

    public static HandlerMethod of(final Object instance, final Method method, final List<MethodArgument> parameters) {
        return new HandlerMethod(instance, method, parameters);
    }

    public HttpResponse invoke(final HttpRequest request, final Map<String, String> pathParams) throws Exception {
        final List<Object> arguments = getArguments(request, pathParams);
        return (HttpResponse) method.invoke(instance, Stream.concat(Stream.of(request), arguments.stream()).toArray());
    }

    private List<Object> getArguments(final HttpRequest request, final Map<String, String> pathParams) {
        final Map<String, String> queryParams = request.getQueryParams();
        return methodArguments.stream()
                .map(methodArgument -> {
                    final String value = methodArgument.isQuery()
                            ? queryParams.get(methodArgument.getName())
                            : pathParams.get(methodArgument.getName());

                    return Optional.ofNullable(value)
                            .map(val -> typeConvert(val, methodArgument.getType()))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Object typeConvert(final String value, final Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == double.class || type == Double.class) {
            return Integer.parseInt(value);
        }
        if (type == float.class || type == Float.class) {
            return Integer.parseInt(value);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }

}
