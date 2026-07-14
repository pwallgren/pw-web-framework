package com.petwal.pwweb.demo;

import com.petwal.pwweb.context.annotation.PwPostConstruct;
import com.petwal.pwweb.context.annotation.PwPreDestroy;

public class TestService {

  private final String message;

  public TestService(final String message) {
    this.message = message;
  }

  @PwPostConstruct
  public void postConstruct() {
    System.out.println("postConstruct is running...");
  }

  @PwPreDestroy
  public void preDestroy() {
    System.out.println("preDestroy is running...");
  }

  public void test() {
    System.out.println(message);
  }

}
