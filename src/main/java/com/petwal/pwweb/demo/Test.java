package com.petwal.pwweb.demo;

import com.petwal.pwweb.context.annotation.PwProperties;

@PwProperties(prefix = "test")
public class Test {

  private String test;

  public Test() {
  }

  public Test(final String test) {
    this.test = test;
  }

  public String getTest() {
    return test;
  }
}
