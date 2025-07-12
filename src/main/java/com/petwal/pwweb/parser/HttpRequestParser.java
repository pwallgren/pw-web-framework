package com.petwal.pwweb.parser;


import com.petwal.pwweb.model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class HttpRequestParser {

    public static final String SPACE = " ";
    public static final String COLON = ":";
    public static final String CONTENT_LENGTH = "Content-Length";

    private HttpRequestParser() {
    }

    public static HttpRequest parse(final BufferedReader reader) throws IOException {
        final String[] parts = getRequestParts(reader.readLine());
        final String method = parts[0];
        final String uri = parts[1];
        final String version = parts[2];

        final Map<String, String> headers = getHeaders(reader);

        final String body = getBody(reader, headers);

        return HttpRequest.builder()
                .method(method)
                .uri(uri)
                .version(version)
                .headers(headers)
                .body(body)
                .build();

    }

    private static String getBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        if (headers.containsKey(CONTENT_LENGTH)) {
            final int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
            final char[] bodyChars = new char[contentLength];
            final int read = reader.read(bodyChars, 0, contentLength);
            if (read != contentLength) {
                throw new IOException("Unexpected end of body");
            }
            return new String(bodyChars);
        }
        return null;
    }

    private static String[] getRequestParts(final String requestLine) {
        final String[] parts = requestLine.split(SPACE);
        final int length = parts.length;
        if (length != 3) {
            throw new IllegalStateException("Request line does not contain the required parts. Shoud be 3, but was " + length);
        }

        return parts;
    }

    private static Map<String, String> getHeaders(final BufferedReader reader) throws IOException {
        return reader.lines()
                .takeWhile(line -> !line.isEmpty())
                .filter(line -> line.indexOf(COLON) > 0)
                .map(HttpRequestParser::toKeyVal)
                .collect(toMap(KeyVal::key, KeyVal::val, (v1, v2) -> v1));
    }

    private static KeyVal toKeyVal(final String line) {
        final int colonIndex = line.indexOf(COLON);
        final String key = line.substring(0, colonIndex).trim();
        final String value = line.substring(colonIndex + 1).trim();
        return new KeyVal(key, value);
    }

    record KeyVal(String key, String val) {

    }
}
