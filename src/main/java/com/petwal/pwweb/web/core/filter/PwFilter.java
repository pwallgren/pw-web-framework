package com.petwal.pwweb.web.core.filter;

import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;

public interface PwFilter {

  HttpResponse intercept(HttpRequest request, FilterChain chain);

  default int order() {
    return 0;
  }

}
