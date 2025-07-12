package com.petwal.pwweb.model;

import java.lang.reflect.Method;

public class HandlerMethod {
    private final Object instance;
    private final Method method;

    private HandlerMethod(final Object instance, final Method method) {
        this.instance = instance;
        this.method = method;
    }

    public static HandlerMethod of(final Object instance, final Method method) {
        return new HandlerMethod(instance, method);
    }

    public HttpResponse invoke(final HttpRequest request) throws Exception {
        return (HttpResponse) method.invoke(instance, request);
    }
}
