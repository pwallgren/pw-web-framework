package com.petwal.pwweb.demo;


import com.petwal.pwweb.PwApplication;

public class Main {

  public static void main(String[] args) {

    final PwApplication application = PwApplication.builder(Main.class)
        .port(8080)
        .basePackage("com.petwal.pwweb")
        .build();
    application.start();
  }

}