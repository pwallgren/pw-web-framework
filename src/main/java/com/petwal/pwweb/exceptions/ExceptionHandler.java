package com.petwal.pwweb.exceptions;

import com.petwal.pwweb.exceptions.model.BadRequestException;
import com.petwal.pwweb.exceptions.model.NotFoundException;
import com.petwal.pwweb.model.HttpResponse;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static HttpResponse handle(Exception ex) {
        if (ex instanceof BadRequestException) {
            return HttpResponse.badRequest().build();
        } else if (ex instanceof NotFoundException) {
            return HttpResponse.notFound().build();
        } else {
            return HttpResponse.internalError().build();
        }
    }
}
