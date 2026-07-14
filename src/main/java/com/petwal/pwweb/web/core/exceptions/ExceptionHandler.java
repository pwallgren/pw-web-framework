package com.petwal.pwweb.web.core.exceptions;

import com.petwal.pwweb.web.http.HttpResponse;

public class ExceptionHandler {

  private ExceptionHandler() {
  }

  public static HttpResponse handle(final Exception ex) {
    return switch (ex) {
      case BadRequestException ignored -> HttpResponse.badRequest().build();
      case NotFoundException ignored -> HttpResponse.notFound().build();
      case InternalErrorException ignored -> HttpResponse.internalError().build();
      case null, default -> HttpResponse.internalError().build();
    };
  }
}
