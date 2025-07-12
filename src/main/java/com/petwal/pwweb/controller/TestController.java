package com.petwal.pwweb.controller;

import com.petwal.pwweb.annotation.PwController;
import com.petwal.pwweb.annotation.PwRoute;
import com.petwal.pwweb.model.HttpRequest;
import com.petwal.pwweb.model.HttpResponse;

@PwController
public class TestController {

    @PwRoute(method = "GET", path = "/test")
    public HttpResponse getTest(final HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(200)
                .body("This is the test response body")
                .build();
    }
}
