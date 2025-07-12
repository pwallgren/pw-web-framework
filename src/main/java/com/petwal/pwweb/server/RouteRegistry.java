package com.petwal.pwweb.server;

import com.petwal.pwweb.annotation.PwController;
import com.petwal.pwweb.annotation.PwRoute;
import com.petwal.pwweb.model.HandlerMethod;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RouteRegistry {

    public static Map<String, HandlerMethod> register(final String controllersPath) {
        final Map<String, HandlerMethod> routes = new HashMap<>();
        final Reflections reflections = new Reflections(controllersPath);

        final Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(PwController.class);
        for (Class<?> clazz : controllerClasses) {
            if (clazz.isAnnotationPresent(PwController.class)) {
                try {
                    final Object instance = clazz.getConstructor().newInstance();
                    for (Method method : clazz.getDeclaredMethods()) {
                        final PwRoute route = method.getAnnotation(PwRoute.class);
                        if (route != null) {
                            final String key = getRouteKey(route.method(), route.path());
                            routes.put(key, HandlerMethod.of(instance, method));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error when initializing the routes", e);
                }
            }
        }
        return routes;
    }

    public static String getRouteKey(final String httpMethod, final String path) {
        return httpMethod.toUpperCase() + " " + path;
    }

}
