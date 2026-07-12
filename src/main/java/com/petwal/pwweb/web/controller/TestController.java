package com.petwal.pwweb.web.controller;

import com.petwal.pwweb.web.annotation.*;
import com.petwal.pwweb.web.http.HttpMethod;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;

import java.util.Map;

@PwController(path = "test")
public class TestController {

  @PwRoute(method = HttpMethod.GET, path = "query/{id}")
  public HttpResponse getQuery(final HttpRequest request, final @PwQuery("name") String name,
      final @PwPath Integer id, final @PwBody TestBody body) {

    // Some logic...
    return HttpResponse.ok()
        .body(body)
        .headers(Map.of("Content-Type", "application/json"))
        .build();
  }

  @PwRoute(method = HttpMethod.POST, path = "/")
  public HttpResponse post(final HttpRequest request, final @PwBody TestBody body) {

    // Some logic...
    return HttpResponse.ok()
        .body(body)
        .headers(Map.of("Content-Type", "application/json"))
        .build();
  }
}
