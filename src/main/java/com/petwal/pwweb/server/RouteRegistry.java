package com.petwal.pwweb.server;

import com.petwal.pwweb.annotation.PwController;
import com.petwal.pwweb.annotation.PwPath;
import com.petwal.pwweb.annotation.PwQuery;
import com.petwal.pwweb.annotation.PwRoute;
import com.petwal.pwweb.model.HandlerMethod;
import com.petwal.pwweb.model.ParameterMeta;
import com.petwal.pwweb.model.RouteEntry;
import com.petwal.pwweb.model.RoutePattern;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class RouteRegistry {

    public static final String SPACE = " ";

    public static List<RouteEntry> register(final String controllersPath) {
        final List<RouteEntry> routes = new ArrayList<>();
        final Reflections reflections = new Reflections(controllersPath);

        final Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(PwController.class);
        for (Class<?> clazz : controllerClasses) {
            if (clazz.isAnnotationPresent(PwController.class)) {
                try {
                    final Object instance = clazz.getConstructor().newInstance();
                    for (Method method : clazz.getDeclaredMethods()) {
                        final PwRoute route = method.getAnnotation(PwRoute.class);
                        if (route != null) {
                            // Build parameter metadata
                            List<ParameterMeta> parameterMetaList = new ArrayList<>();
                            Parameter[] parameters = method.getParameters();
                            for (Parameter parameter : parameters) {
                                final PwPath pathAnnotation = parameter.getAnnotation(PwPath.class);
                                final PwQuery queryAnnotation = parameter.getAnnotation(PwQuery.class);
                                if (pathAnnotation != null) {
                                    final String name = pathAnnotation.value().isEmpty()
                                            ? parameter.getName()
                                            : pathAnnotation.value();
                                    parameterMetaList.add(ParameterMeta.path(name, parameter.getType()));
                                } else if (queryAnnotation != null) {
                                    final String name = queryAnnotation.value().isEmpty()
                                            ? parameter.getName()
                                            : queryAnnotation.value();
                                    parameterMetaList.add(ParameterMeta.query(name, parameter.getType()));
                                }
                            }

                            routes.add(RouteEntry.builder()
                                    .httpMethod(route.method())
                                    .uri(new RoutePattern(route.path()))
                                    .handlerMethod(HandlerMethod.of(instance, method, parameterMetaList))
                                    .build());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error when initializing the routes", e);
                }
            }
        }
        return routes;
    }

}
