package com.petwal.pwweb.core;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.petwal.pwweb.core.exceptions.ExceptionHandler;
import com.petwal.pwweb.core.exceptions.NotFoundException;
import com.petwal.pwweb.core.route.RouteEntry;
import com.petwal.pwweb.core.route.RouteRegistry;
import com.petwal.pwweb.http.HttpRequest;
import com.petwal.pwweb.http.HttpRequestParser;
import com.petwal.pwweb.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

    private final List<RouteEntry> routes;
    private final ResponseWriter responseWriter;

    public Dispatcher(final String controllersPath) {
        this.routes = RouteRegistry.register(controllersPath);
        this.responseWriter = new ResponseWriter(createDefaultMapper());
    }

    public Dispatcher(final String controllersPath, final ObjectMapper objectMapper) {
        this.routes = RouteRegistry.register(controllersPath);
        this.responseWriter = new ResponseWriter(objectMapper);
    }

    public void handle(final Socket socket) {
        BufferedReader inputStream = null;
        BufferedWriter outputStream = null;
        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            final HttpRequest request = HttpRequestParser.parse(inputStream);
            final RouteEntry routeEntry = getMatchingRoute(request);
            final HttpResponse response = routeEntry.invokeHandler(request);
            responseWriter.send(response, outputStream);
        } catch (Exception ex) {
            handleExceptions(ex, outputStream);
        } finally {
            closeStreams(inputStream, outputStream, socket);
        }
    }

    public List<RouteEntry> getRoutes() {
        return routes;
    }

    private RouteEntry getMatchingRoute(final HttpRequest request) {
        return routes.stream()
                .filter(entry -> entry.getHttpMethod().equalsIgnoreCase(request.getMethod()))
                .filter(route -> route.getPattern().match(request.getPath()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Handler method for request not found"));
    }

    private void handleExceptions(final Exception exception, final BufferedWriter outputStream) {
        LOGGER.error("Exception encountered", exception);
        try {
            responseWriter.send(ExceptionHandler.handle(exception), outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void closeStreams(final BufferedReader inputStream, final BufferedWriter outputStream, final Socket socket) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to close inputStream: {} ", e.getMessage());
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to close outputStream: {} ", e.getMessage());
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to close socket: {} ", e.getMessage());
        }
    }

    private ObjectMapper createDefaultMapper() {
        return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
