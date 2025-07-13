package com.petwal.pwweb.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.joining;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(10, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));

    private final int port;
    private final Dispatcher dispatcher;

    public Server(final int port, final Dispatcher dispatcher) {
        this.port = port;
        this.dispatcher = dispatcher;
    }

    public void start() {
        LOGGER.info("Starting up server on port {}", port);
        LOGGER.info("Dispatching requests for routes: {}", getRouteString());

        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                runAsync(() -> dispatcher.handle(socket), EXECUTOR);
            }
        } catch (IOException e) {
            LOGGER.error("Server error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getRouteString() {
        return dispatcher.getRoutes()
                .stream()
                .map(route -> route.getHttpMethod() + " " + route.getPattern().getOriginalPattern())
                .collect(joining(", "));
    }


}
