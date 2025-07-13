package com.petwal.pwweb.controller;

import com.petwal.pwweb.annotation.PwController;
import com.petwal.pwweb.annotation.PwPath;
import com.petwal.pwweb.annotation.PwQuery;
import com.petwal.pwweb.annotation.PwRoute;
import com.petwal.pwweb.model.HttpRequest;
import com.petwal.pwweb.model.HttpResponse;

import java.util.Map;
import java.util.Optional;

@PwController
public class TestController {

    private final Map<Integer, String> DB_NAME = Map.of(
            1, "Petter",
            2, "Janne"
    );

    private final Map<String, String> DB_SURNAME = Map.of(
            "Petter", "Wallgren",
            "Janne", "Kallesson"
    );

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

    @PwRoute(method = "GET", path = "/query/{id}")
    public HttpResponse getWithQuery(final HttpRequest request, final @PwQuery("name") String name, final @PwPath Integer id) {

        final String dbName = Optional.ofNullable(DB_NAME.get(id))
                .orElse("unknown");
        final String dbSurname = Optional.ofNullable(DB_SURNAME.get(name))
                .orElse("unknown");

        return HttpResponse.builder()
                .statusCode(200)
                .statusMessage("OK")
                .body("""
                        {
                            "name": "%s",
                            "surname": "%s"
                        }
                        """.formatted(dbName, dbSurname))
                .headers(Map.of("Content-Type", "application/json"))
                .build();
    }
}
