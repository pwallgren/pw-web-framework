package com.petwal.pwweb.core.exceptions;

import com.petwal.pwweb.http.HttpResponse;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static HttpResponse handle(Exception ex) {
        if (ex instanceof BadRequestException) {
            return HttpResponse.badRequest().build();
        } else if (ex instanceof NotFoundException) {
            return HttpResponse.notFound().build();
        } else if (ex instanceof InternalErrorException) {
            return HttpResponse.internalError().build();
        } else {
            return HttpResponse.internalError().build();
        }
    }
}
