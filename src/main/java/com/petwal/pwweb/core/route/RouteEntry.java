package com.petwal.pwweb.core.route;

import com.petwal.pwweb.http.HttpMethod;
import com.petwal.pwweb.http.HttpRequest;
import com.petwal.pwweb.http.HttpResponse;

import java.util.Map;

import static com.petwal.pwweb.util.Check.notNull;

public class RouteEntry {
    private final HttpMethod httpMethod;
    private final RoutePattern pattern;
    private final HandlerMethod handlerMethod;

    public RouteEntry(final Builder builder) {
        this.httpMethod = notNull("httpMethod", builder.httpMethod);
        this.pattern = notNull("pattern", builder.pattern);
        this.handlerMethod = notNull("handlerMethod", builder.handlerMethod);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RoutePattern getPattern() {
        return pattern;
    }

    public HttpResponse invokeHandler(final HttpRequest request) throws Exception {
        final Map<String, String> pathParams = pattern.getParamMappings(request.getPath());
        return handlerMethod.invoke(request, pathParams);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HttpMethod httpMethod;                  // GET, POST, etc.
        private RoutePattern pattern;               // the compiled pattern
        private HandlerMethod handlerMethod;

        public Builder httpMethod(final HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder uri(final RoutePattern pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder handlerMethod(final HandlerMethod handlerMethod) {
            this.handlerMethod = handlerMethod;
            return this;
        }

        public RouteEntry build() {
            return new RouteEntry(this);
        }

    }
}
