package com.petwal.pwweb.demo;

public class TestService {

  private final String message;

  public TestService(final String message) {
    this.message = message;
  }

  public void test() {
    System.out.println(message);
  }

}
