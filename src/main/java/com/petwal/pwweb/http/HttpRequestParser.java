package com.petwal.pwweb.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class HttpRequestParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestParser.class);

    public static final String SPACE = " ";
    public static final String COLON = ":";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final char QUESTION_MARK = '?';
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";

    private HttpRequestParser() {
    }

    public static HttpRequest parse(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            LOGGER.error("Encountered empty request");
            throw new IllegalStateException("Encountered empty request");
        }

        final String[] parts = requestLine.split(SPACE);
        if (parts.length != 3) {
            LOGGER.error("Request with invalid request line encountered: {}", requestLine);
        }

        final HttpMethod method = HttpMethod.valueOf(parts[0].toUpperCase());
        final String uri = parts[1];
        final String version = parts[2];

        final Map<String, String> headers = getHeaders(reader);
        final Map<String, String> queryParams = getQueryParams(uri);
        final String body = getBody(reader, headers);

        return HttpRequest.builder()
                .method(method)
                .uri(uri)
                .version(version)
                .headers(headers)
                .queryParams(queryParams)
                .body(body)
                .build();
    }

    private static String getBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        if (headers.containsKey(CONTENT_LENGTH)) {
            final int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
            final char[] bodyChars = new char[contentLength];
            final int read = reader.read(bodyChars, 0, contentLength);
            if (read != contentLength) {
                throw new IllegalStateException("Content length value does not match request body");
            }
            return new String(bodyChars);
        }
        return null;
    }

    private static Map<String, String> getHeaders(final BufferedReader reader) {
        return reader.lines()
                .takeWhile(line -> !line.isEmpty())
                .filter(line -> line.indexOf(COLON) > 0)
                .map(HttpRequestParser::toHeader)
                .collect(toMap(Header::key, Header::val, (v1, v2) -> v1));
    }

    private static Map<String, String> getQueryParams(final String uri) {
        if (!uri.contains(String.valueOf(QUESTION_MARK))) {
            return Map.of();
        }
        final String queryString = uri.substring(uri.indexOf(QUESTION_MARK));
        return Arrays.stream(queryString.substring(1).split(AMPERSAND))
                .map(query -> query.split(EQUALS))
                .collect(toMap(parts -> parts[0], parts -> parts[1]));
    }

    private static Header toHeader(final String line) {
        final int colonIndex = line.indexOf(COLON);
        final String key = line.substring(0, colonIndex).trim();
        final String value = line.substring(colonIndex + 1).trim();
        return new Header(key, value);
    }

    private record Header(String key, String val) {

    }
}
