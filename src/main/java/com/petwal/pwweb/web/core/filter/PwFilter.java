package com.petwal.pwweb.web.core.filter;

import com.petwal.pwweb.web.core.route.RequestContext;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;

public interface PwFilter {

  HttpResponse intercept(RequestContext requestContext, FilterChain chain);

  default int order() {
    return 0;
  }

}
