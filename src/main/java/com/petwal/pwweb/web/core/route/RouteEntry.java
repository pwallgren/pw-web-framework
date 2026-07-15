package com.petwal.pwweb.web.core.route;

import com.petwal.pwweb.web.http.HttpMethod;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;

import java.util.Map;

import static com.petwal.pwweb.util.Check.notNull;

public class RouteEntry {

  private final HttpMethod httpMethod;
  private final RoutePattern pattern;
  private final HandlerMethod handlerMethod;
  private final Boolean requireAuth;

  public RouteEntry(final Builder builder) {
    this.httpMethod = notNull("httpMethod", builder.httpMethod);
    this.pattern = notNull("pattern", builder.pattern);
    this.handlerMethod = notNull("handlerMethod", builder.handlerMethod);
    this.requireAuth = notNull("requireAuth", builder.requireAuth);
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public RoutePattern getPattern() {
    return pattern;
  }

  public HttpResponse invokeHandler(final RequestContext requestContext) throws Exception {
    final HttpRequest request = requestContext.getRequest();
    final Map<String, String> pathParams = pattern.getParamMappings(request.getPath());
    return handlerMethod.invoke(requestContext, pathParams);
  }

  public Boolean requireAuth() {
    return requireAuth;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private HttpMethod httpMethod;                  // GET, POST, etc.
    private RoutePattern pattern;               // the compiled pattern
    private HandlerMethod handlerMethod;
    private Boolean requireAuth;

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

    public Builder requireAuth(final boolean requireAuth) {
      this.requireAuth = requireAuth;
      return this;
    }

    public RouteEntry build() {
      return new RouteEntry(this);
    }

  }
}
