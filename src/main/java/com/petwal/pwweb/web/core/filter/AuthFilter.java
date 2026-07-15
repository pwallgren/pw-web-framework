package com.petwal.pwweb.web.core.filter;

import com.petwal.pwweb.context.annotation.PwComponent;
import com.petwal.pwweb.web.core.route.RequestContext;
import com.petwal.pwweb.web.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@PwComponent
public class AuthFilter implements PwFilter {

  private final PwAuthenticator authenticator;

  public AuthFilter(final PwAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  @Override
  public HttpResponse intercept(final RequestContext requestContext, final FilterChain chain) {

    if (!requestContext.requireAuth()) {
      return chain.next(requestContext);
    }

    final Optional<?> principal = authenticator.authenticate(requestContext.getRequest());

    if (principal.isEmpty()) {
      return unauthorized();
    }

    requestContext.setPrincipal(principal.get());
    return chain.next(requestContext);
  }

  @Override
  public int order() {
    return 10;
  }

  private HttpResponse unauthorized() {
    return HttpResponse.builder()
        .statusCode(401)
        .statusMessage("Unauthorized")
        .headers(Map.of("Content-Type", "text/plain"))
        .build();
  }

}
