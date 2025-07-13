package com.petwal.pwweb.core.exceptions;

public class InternalErrorException extends RuntimeException {

    public InternalErrorException(final String message) {
        super(message);
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
