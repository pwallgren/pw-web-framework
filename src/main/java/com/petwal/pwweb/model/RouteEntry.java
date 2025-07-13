package com.petwal.pwweb.model;

import java.util.Map;

public class RouteEntry {
    private final String httpMethod;
    private final RoutePattern pattern;
    private final HandlerMethod handlerMethod;

    public RouteEntry(final Builder builder) {
        this.httpMethod = builder.httpMethod;
        this.pattern = builder.pattern;
        this.handlerMethod = builder.handlerMethod;
    }

    public String getHttpMethod() {
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
        private String httpMethod;                  // GET, POST, etc.
        private RoutePattern pattern;               // the compiled pattern
        private HandlerMethod handlerMethod;

        public Builder httpMethod(final String httpMethod) {
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
