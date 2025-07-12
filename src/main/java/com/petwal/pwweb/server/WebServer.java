package com.petwal.pwweb.server;


import com.petwal.pwweb.annotation.PwController;
import com.petwal.pwweb.annotation.PwRoute;
import com.petwal.pwweb.model.HandlerMethod;
import com.petwal.pwweb.model.HttpRequest;
import com.petwal.pwweb.model.HttpResponse;
import com.petwal.pwweb.parser.HttpRequestParser;
import org.reflections.Reflections;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebServer {

    private final Map<String, HandlerMethod> routes;

    public WebServer() {
        this.routes = new HashMap<>();
    }

    public void registerRoutes(final String packagePath) {
        final Reflections reflections = new Reflections(packagePath);

        final Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(PwController.class);
        for (Class<?> clazz : controllerClasses) {
            if (clazz.isAnnotationPresent(PwController.class)) {
                try {
                    final Object instance = clazz.getConstructor().newInstance();
                    for (Method method : clazz.getDeclaredMethods()) {
                        final PwRoute route = method.getAnnotation(PwRoute.class);
                        if (route != null) {
                            String key = toRouteKey(route.method(), route.path());
                            routes.put(key, HandlerMethod.of(instance, method));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error when initializing the routes", e);
                }
            }
        }
    }

    public void start(final int port) throws Exception {

        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (final Socket socket = serverSocket.accept();
                     final BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     final BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    final HttpRequest request = HttpRequestParser.parse(inputStream);

                    final HandlerMethod handlerMethod = routes.get(toRouteKey(request.getMethod(), request.getUri()));
                    final HttpResponse response = handlerMethod.invoke(request);

                    writeTo(response, outputStream);
                    outputStream.flush();
                }
            }

        }

    }

    public void writeTo(final HttpResponse response, final BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 " + response.getStatusCode() + "\r\n");
        if (response.getHeaders() != null) {
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                out.write(header.getKey() + ": " + header.getValue() + "\r\n");
            }
        }
        out.write("\r\n");
        if (response.getBody() != null) {
            out.write(response.getBody());
        }
        out.flush();
    }

    private static String toRouteKey(final String method, final String path) {
        return method.toUpperCase() + " " + path;
    }
}
