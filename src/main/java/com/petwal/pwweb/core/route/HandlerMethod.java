package com.petwal.pwweb.core.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petwal.pwweb.http.HttpRequest;
import com.petwal.pwweb.http.HttpResponse;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return (HttpResponse) method.invoke(instance, arguments.toArray());
    }

    private List<Object> getArguments(final HttpRequest request, final Map<String, String> pathParams) {
        final Map<String, String> queryParams = request.getQueryParams();
        return methodArguments.stream()
                .map(methodArgument -> {
                    if (methodArgument.isHttpRequest()) {
                        return request;
                    }
                    if (methodArgument.isBody()) {
                        return request.getBody()
                                .map(body -> toObject(methodArgument, body))
                                .orElse(null);
                    }

                    String value = null;
                    if (methodArgument.isQuery()) {
                        value = queryParams.get(methodArgument.getName());
                    } else if (methodArgument.isPath()) {
                        value = pathParams.get(methodArgument.getName());
                    } else if (methodArgument.isBody()) {
                        value = pathParams.get(methodArgument.getName());
                    }

                    return Optional.ofNullable(value)
                            .map(val -> typeConvert(val, methodArgument.getType()))
                            .orElse(null);
                })
                .toList();
    }

    private static Object toObject(final MethodArgument methodArgument, final String body) {
        try {
            return methodArgument.getObjectMapper().readValue(body, methodArgument.getType());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
