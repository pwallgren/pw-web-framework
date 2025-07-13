package com.petwal.pwweb.server;


import com.petwal.pwweb.exceptions.ExceptionHandler;
import com.petwal.pwweb.exceptions.model.BadRequestException;
import com.petwal.pwweb.exceptions.model.NotFoundException;
import com.petwal.pwweb.model.HttpRequest;
import com.petwal.pwweb.model.HttpResponse;
import com.petwal.pwweb.model.RouteEntry;
import com.petwal.pwweb.parser.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.runAsync;

public class WebServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(10, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));
    public static final String HTTP_1_1 = "HTTP/1.1";
    public static final String CRLF = "\r\n";

    private List<RouteEntry> routeEntries;

    public WebServer() {
        this.routeEntries = new ArrayList<>();
    }

    public void start(final int port, final String controllersPath) throws Exception {
        routeEntries = RouteRegistry.register(controllersPath);
        final String routeString = routeEntries.stream()
                .map(route -> route.getPattern().getOriginalPattern())
                .collect(Collectors.joining(", "));
        LOGGER.info("Starting up server on port {}. Registered routes: {}", port, routeString);

        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                runAsync(() -> {
                    BufferedReader inputStream = null;
                    BufferedWriter outputStream = null;
                    try {
                        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        final HttpRequest request = parseRequest(inputStream);
                        final RouteEntry routeEntry = getRouteEntry(request);
                        final HttpResponse response = routeEntry.invokeHandler(request);

                        sendResponse(response, outputStream);
                    } catch (Exception ex) {
                        handleExceptions(ex, outputStream);
                    } finally {
                        closeStreams(inputStream, outputStream, socket);
                    }
                }, EXECUTOR);
            }
        }
    }

    private RouteEntry getRouteEntry(final HttpRequest request) throws NotFoundException {
        return routeEntries.stream()
                .filter(entry -> entry.getHttpMethod().equalsIgnoreCase(request.getMethod()))
                .filter(route -> route.getPattern().match(request.getPath()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Handler method for request not found"));
    }

    private HttpRequest parseRequest(final BufferedReader inputStream) throws IOException, BadRequestException {
        final HttpRequest request = HttpRequestParser.parse(inputStream);
        if (request == null) {
            throw new BadRequestException("Invalid request");
        }
        return request;
    }

    public void sendResponse(final HttpResponse response, final BufferedWriter outputStream) throws IOException {
        outputStream.write(HTTP_1_1 + " " + response.getStatusCode() + " " + response.getStatusMessage() + CRLF);
        if (response.getHeaders() != null) {
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                outputStream.write(header.getKey() + ": " + header.getValue() + CRLF);
            }
        }
        outputStream.write(CRLF);
        if (response.getBody() != null) {
            outputStream.write(response.getBody());
        }
        outputStream.flush();
    }

    private void handleExceptions(final Exception e, final BufferedWriter outputStream) {
        LOGGER.error("Exception encountered: {} ", e.getMessage());
        try {
            sendResponse(ExceptionHandler.handle(e), outputStream);
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    private static void closeStreams(final BufferedReader inputStream, final BufferedWriter outputStream, final Socket socket) {
        try {
            if (inputStream != null) inputStream.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close input: {} ", e.getMessage());
        }
        try {
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close output: {} ", e.getMessage());
        }
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close socket: {} ", e.getMessage());
        }
    }
}
