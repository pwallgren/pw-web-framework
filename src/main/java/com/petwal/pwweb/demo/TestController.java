package com.petwal.pwweb.demo;

import com.petwal.pwweb.web.annotation.PwBody;
import com.petwal.pwweb.web.annotation.PwController;
import com.petwal.pwweb.web.annotation.PwPath;
import com.petwal.pwweb.web.annotation.PwQuery;
import com.petwal.pwweb.web.annotation.PwRoute;
import com.petwal.pwweb.web.http.HttpMethod;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpResponse;
import java.util.Map;

@PwController(path = "test")
public class TestController {

  private final TestService testService;

  public TestController(final TestService testService) {
    this.testService = testService;
  }

  @PwRoute(method = HttpMethod.GET, path = "query/{id}")
  public HttpResponse getQuery(final HttpRequest request, final @PwQuery("name") String name,
      final @PwPath Integer id, final @PwBody TestBody body) {

    testService.test();
    // Some logic...
    return HttpResponse.ok()
        .body(body)
        .headers(Map.of("Content-Type", "application/json"))
        .build();
  }

  @PwRoute(method = HttpMethod.POST, path = "/")
  public HttpResponse post(final HttpRequest request, final @PwBody TestBody body) {

    testService.test();

    // Some logic...
    return HttpResponse.ok()
        .body(body)
        .headers(Map.of("Content-Type", "application/json"))
        .build();
  }
}
