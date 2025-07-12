package com.petwal.pwweb.server;


import com.petwal.pwweb.exceptions.ExceptionHandler;
import com.petwal.pwweb.exceptions.model.BadRequestException;
import com.petwal.pwweb.exceptions.model.NotFoundException;
import com.petwal.pwweb.model.HandlerMethod;
import com.petwal.pwweb.model.HttpRequest;
import com.petwal.pwweb.model.HttpResponse;
import com.petwal.pwweb.parser.HttpRequestParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class WebServer {

    public static final String HTTP_1_1 = "HTTP/1.1";
    private Map<String, HandlerMethod> routes;

    public WebServer() {
        this.routes = new HashMap<>();
    }

    public void start(final int port, final String controllersPath) throws Exception {
        routes = RouteRegistry.register(controllersPath);
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = null;
                BufferedReader inputStream = null;
                BufferedWriter outputStream = null;
                try {
                    socket = serverSocket.accept();
                    inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    final HttpRequest request = parseRequest(inputStream);
                    final HandlerMethod handlerMethod = getHandlerMethod(request);
                    final HttpResponse response = handlerMethod.invoke(request);

                    sendResponse(response, outputStream);
                } catch (Exception ex) {
                    System.out.println("Exception encountered: " + ex);
                    final HttpResponse response = ExceptionHandler.handle(ex);
                    if (outputStream != null) {
                        sendResponse(response, outputStream);
                    }
                } finally {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                    if (socket != null) socket.close();
                }
            }

        }

    }

    private HandlerMethod getHandlerMethod(final HttpRequest request) throws NotFoundException {
        final HandlerMethod handlerMethod = routes.get(toRouteKey(request.getMethod(), request.getUri()));
        if (handlerMethod == null) {
            throw new NotFoundException("Handler method for route not found");
        }
        return handlerMethod;
    }

    public HttpRequest parseRequest(final BufferedReader inputStream) throws IOException, BadRequestException {
        final HttpRequest request = HttpRequestParser.parse(inputStream);
        if (request == null) {
            throw new BadRequestException("Invalid request");
        }
        return request;
    }

    public void sendResponse(final HttpResponse response, final BufferedWriter out) throws IOException {
        out.write(HTTP_1_1 + " " + response.getStatusCode() + " " + response.getStatusMessage() + "\r\n");
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
