package com.petwal.pwweb.web.core.exceptions;

public class NotFoundException extends RuntimeException {

  public NotFoundException(final String message) {
    super(message);
  }

  @Override
  public String toString() {
    return this.getMessage();
  }
}
