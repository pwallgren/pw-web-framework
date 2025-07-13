package com.petwal.pwweb.model;

import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    public static final char QUESTION_MARK = '?';

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;

    public HttpRequest(final Builder builder) {
        this.method = builder.method;
        this.uri = builder.uri;
        this.version = builder.version;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;
        this.body = builder.body;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
    }

    public String getPath() {
        long count = uri.chars()
                .filter(ch -> ch == QUESTION_MARK)
                .count();

        if (count > 1) {
            throw new IllegalStateException("uri contains more then one question mark (?)");
        }

        final int indexOf = uri.indexOf(QUESTION_MARK);
        return uri.substring(0, indexOf);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String method;
        private String uri;
        private String version;
        private Map<String, String> headers;
        private Map<String, String> queryParams;
        private String body;

        public Builder method(final String method) {
            this.method = method;
            return this;
        }

        public Builder uri(final String uri) {
            this.uri = uri;
            return this;
        }

        public Builder version(final String version) {
            this.version = version;
            return this;
        }

        public Builder headers(final Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder queryParams(final Map<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final HttpRequest that = (HttpRequest) o;
        return Objects.equals(method, that.method)
                && Objects.equals(uri, that.uri)
                && Objects.equals(version, that.version)
                && Objects.equals(headers, that.headers)
                && Objects.equals(queryParams, that.queryParams)
                && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uri, version, headers, queryParams, body);
    }
}
