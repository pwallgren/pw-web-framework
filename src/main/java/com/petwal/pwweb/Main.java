package com.petwal.pwweb;


import com.petwal.pwweb.web.core.Dispatcher;
import com.petwal.pwweb.web.core.Server;

public class Main {

  public static void main(String[] args) {
    final Dispatcher dispatcher = new Dispatcher("com.petwal.pwweb.web.controller");
    final Server server = new Server(5000, dispatcher);
    server.start();
  }
}