package com.petwal.pwweb.exceptions.model;

public class BadRequestException extends Exception {

    public BadRequestException(final String message) {
        super(message);
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
