package com.petwal.pwweb.controller;

import com.petwal.pwweb.annotation.PwController;
import com.petwal.pwweb.annotation.PwRoute;
import com.petwal.pwweb.model.HttpRequest;
import com.petwal.pwweb.model.HttpResponse;

import java.util.Map;

@PwController
public class TestController {

    @PwRoute(method = "GET", path = "/test")
    public HttpResponse getTest(final HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(200)
                .statusMessage("OK")
                .body("""
                        {
                            "name": "petter"
                        }
                        """)
                .headers(Map.of("Content-Type", "application/json"))
                .build();
    }
}
