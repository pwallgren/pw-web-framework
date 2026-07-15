package com.petwal.pwweb.web.core.filter;

import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;

@FunctionalInterface
public interface FilterChain {

  HttpResponse next(HttpRequest request);

}
