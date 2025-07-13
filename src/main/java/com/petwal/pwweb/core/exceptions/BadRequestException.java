package com.petwal.pwweb.core.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(final String message) {
        super(message);
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
