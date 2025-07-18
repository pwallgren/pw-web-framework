package com.petwal.pwweb.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petwal.pwweb.http.HttpResponse;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import static com.petwal.pwweb.util.Check.notNull;

public class ResponseWriter {

    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private final ObjectMapper objectMapper;

    public ResponseWriter(final ObjectMapper objectMapper) {
        this.objectMapper = notNull("objectMapper", objectMapper);
    }

    public void send(final HttpResponse response, final BufferedWriter outputStream) throws IOException {
        outputStream.write(HTTP_1_1 + " " + response.getStatusCode() + " " + response.getStatusMessage() + CRLF);
        if (response.getHeaders() != null) {
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                outputStream.write(header.getKey() + ": " + header.getValue() + CRLF);
            }
        }
        outputStream.write(CRLF);
        response.getBody()
                .ifPresent(body -> {
                    try {
                        outputStream.write(toJson(body));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        outputStream.flush();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
