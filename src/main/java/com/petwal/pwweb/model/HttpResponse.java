package com.petwal.pwweb.model;

import java.util.Map;
import java.util.Objects;

public class HttpResponse {

    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(final Builder builder) {
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static HttpResponse.Builder badRequest() {
        return HttpResponse.builder()
                .statusCode(400)
                .headers(Map.of("Content-Type", "text/plain"))
                .body("Bad Request");
    }

    public static HttpResponse.Builder notFound() {
        return HttpResponse.builder()
                .statusCode(404)
                .headers(Map.of("Content-Type", "text/plain"))
                .body("Not Found");
    }

    public static HttpResponse.Builder internalError() {
        return HttpResponse.builder()
                .statusCode(500)
                .headers(Map.of("Content-Type", "text/plain"))
                .body("Internal Server Error");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int statusCode;
        private String statusMessage;
        private Map<String, String> headers;
        private String body;

        public Builder statusCode(final int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusMessage(final String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder headers(final Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final HttpResponse that = (HttpResponse) o;
        return statusCode == that.statusCode
                && Objects.equals(statusMessage, that.statusMessage)
                && Objects.equals(headers, that.headers)
                && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, statusMessage, headers, body);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", statusMessage=" + statusMessage +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
