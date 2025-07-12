package com.petwal.pwweb.exceptions.model;

public class NotFoundException extends Exception {
    public NotFoundException(final String message) {
        super(message);
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
