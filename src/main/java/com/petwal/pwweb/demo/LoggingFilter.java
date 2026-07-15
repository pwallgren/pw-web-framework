package com.petwal.pwweb.demo;

import com.petwal.pwweb.context.annotation.PwComponent;
import com.petwal.pwweb.web.core.filter.FilterChain;
import com.petwal.pwweb.web.core.filter.PwFilter;
import com.petwal.pwweb.web.core.route.RequestContext;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PwComponent
public class LoggingFilter implements PwFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  public HttpResponse intercept(final RequestContext requestContext, final FilterChain chain){

    final HttpRequest request = requestContext.getRequest();

    final long start = System.nanoTime();
    final HttpResponse response = chain.next(requestContext);
    final long durationMs = (System.nanoTime() - start) / 1_000_000;

    LOGGER.info("{} {} -> {} ({} ms)", request.getMethod(), request.getPath(),
        response.getStatusCode(), durationMs);

    return response;
  }

  @Override
  public int order() {
    return 0;
  }

}
