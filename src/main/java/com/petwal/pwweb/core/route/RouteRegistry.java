package com.petwal.pwweb.core.route;

import com.petwal.pwweb.annotations.PwController;
import com.petwal.pwweb.annotations.PwPath;
import com.petwal.pwweb.annotations.PwQuery;
import com.petwal.pwweb.annotations.PwRoute;
import com.petwal.pwweb.http.HttpRequest;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RouteRegistry {

    public static final String SLASH = "/";

    public static List<RouteEntry> register(final String controllersPath) {
        final List<RouteEntry> routes = new ArrayList<>();
        final Reflections reflections = new Reflections(controllersPath);

        final Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(PwController.class);
        for (Class<?> clazz : controllerClasses) {
            if (clazz.isAnnotationPresent(PwController.class)) {
                try {
                    final Object instance = clazz.getConstructor().newInstance();
                    final PwController controller = clazz.getAnnotation(PwController.class);
                    for (Method method : clazz.getDeclaredMethods()) {
                        final PwRoute route = method.getAnnotation(PwRoute.class);
                        if (route != null) {
                            final List<MethodArgument> methodArgumentList = new ArrayList<>();
                            final Parameter[] parameters = method.getParameters();
                            for (Parameter parameter : parameters) {
                                final PwPath pathAnnotation = parameter.getAnnotation(PwPath.class);
                                final PwQuery queryAnnotation = parameter.getAnnotation(PwQuery.class);
                                if (pathAnnotation != null) {
                                    final String name = pathAnnotation.value().isEmpty()
                                            ? parameter.getName()
                                            : pathAnnotation.value();
                                    methodArgumentList.add(MethodArgument.path(name, parameter.getType()));
                                } else if (queryAnnotation != null) {
                                    final String name = queryAnnotation.value().isEmpty()
                                            ? parameter.getName()
                                            : queryAnnotation.value();
                                    methodArgumentList.add(MethodArgument.query(name, parameter.getType()));
                                } else if (parameter.getType().equals(HttpRequest.class)) {
                                    methodArgumentList.add(MethodArgument.request());
                                }
                                else {
                                    throw new IllegalStateException("Unsupported parameter: " + parameter);
                                }
                            }

                            final String controllerPath = trimLeadingAndTrailingSlashes(controller.path());
                            final String routePath = trimLeadingAndTrailingSlashes(route.path());
                            final String fullPath = String.format("/%s/%s", controllerPath, routePath);

                            routes.add(RouteEntry.builder()
                                    .httpMethod(route.method())
                                    .uri(new RoutePattern(fullPath))
                                    .handlerMethod(HandlerMethod.of(instance, method, methodArgumentList))
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

    private static String trimLeadingAndTrailingSlashes(final String path) {
        String trimmedPath = path;
        if (path.startsWith(SLASH)) {
            trimmedPath = path.substring(1);
        }
        if (path.endsWith(SLASH)) {
            trimmedPath = path.substring(path.length() - 1);
        }
        return trimmedPath;
    }

}
