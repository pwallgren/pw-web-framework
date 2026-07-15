package com.petwal.pwweb.web.core.filter;

import com.petwal.pwweb.web.core.route.RequestContext;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;

@FunctionalInterface
public interface FilterChain {

  HttpResponse next(RequestContext request);

}
