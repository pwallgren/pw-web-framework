package com.petwal.pwweb.web.core.route;

import com.petwal.pwweb.util.Check;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;
import java.util.Optional;

public class RequestContext {

  private final HttpRequest request;
  private final RouteEntry routeEntry;
  private Object principal;

  private RequestContext(final Builder builder) {
    request = Check.notNull("request", builder.request);
    routeEntry = builder.routeEntry;
    principal = builder.principal;
  }

  public HttpRequest getRequest() {
    return request;
  }

  public Optional<RouteEntry> getRouteEntry() {
    return Optional.ofNullable(routeEntry);
  }

  public Optional<Object> getPrincipal() {
    return Optional.ofNullable(principal);
  }

  public void setPrincipal(final Object principal) {
    this.principal = principal;
  }

  public HttpResponse invokeHandler() throws Exception {
    return routeEntry.invokeHandler(this);
  }

  public boolean requireAuth() {
    return getRouteEntry()
        .map(RouteEntry::requireAuth)
        .orElse(false);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private HttpRequest request;
    private RouteEntry routeEntry;
    private Object principal;

    private Builder() {
    }

    public Builder request(final HttpRequest val) {
      request = val;
      return this;
    }

    public Builder routeEntry(final RouteEntry val) {
      routeEntry = val;
      return this;
    }

    public Builder principal(final Object val) {
      principal = val;
      return this;
    }

    public RequestContext build() {
      return new RequestContext(this);
    }
  }
}
