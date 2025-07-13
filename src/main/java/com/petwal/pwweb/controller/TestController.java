package com.petwal.pwweb.controller;

import com.petwal.pwweb.annotations.PwController;
import com.petwal.pwweb.annotations.PwPath;
import com.petwal.pwweb.annotations.PwQuery;
import com.petwal.pwweb.annotations.PwRoute;
import com.petwal.pwweb.http.HttpRequest;
import com.petwal.pwweb.http.HttpResponse;

import java.util.Map;

@PwController(path = "test")
public class TestController {

    @PwRoute(method = "GET", path = "test")
    public HttpResponse getTest(final HttpRequest request) {

        // Some logic...

        return HttpResponse.ok()
                .body(new TestUser(1337, "Janne"))
                .headers(Map.of("Content-Type", "application/json"))
                .build();
    }

    @PwRoute(method = "GET", path = "query/{id}")
    public HttpResponse getWithQuery(final HttpRequest request, final @PwQuery("name") String name, final @PwPath Integer id) {

        // Some logic...

        return HttpResponse.ok()
                .body(new TestUser(id, name))
                .headers(Map.of("Content-Type", "application/json"))
                .build();
    }
}
