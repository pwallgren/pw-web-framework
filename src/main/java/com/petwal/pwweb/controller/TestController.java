package com.petwal.pwweb.controller;

import com.petwal.pwweb.annotations.*;
import com.petwal.pwweb.http.HttpMethod;
import com.petwal.pwweb.http.HttpRequest;
import com.petwal.pwweb.http.HttpResponse;

import java.util.Map;

@PwController(path = "test")
public class TestController {

    @PwRoute(method = HttpMethod.GET, path = "query/{id}")
    public HttpResponse getQuery(final HttpRequest request, final @PwQuery("name") String name, final @PwPath Integer id, final @PwBody TestBody body) {

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
