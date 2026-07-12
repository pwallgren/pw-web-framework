package com.petwal.pwweb;


import com.petwal.pwweb.context.core.BeanRegistry;
import com.petwal.pwweb.demo.Horse;
import com.petwal.pwweb.demo.Legs;
import com.petwal.pwweb.web.core.Dispatcher;
import com.petwal.pwweb.web.core.Server;

public class Main {

  public static void main(String[] args) {
    final BeanRegistry beanRegistry = new BeanRegistry();
    beanRegistry.scan("com.petwal.pwweb");

    final Horse horse = beanRegistry.getBean(Horse.class);
    final Legs Legs = beanRegistry.getBean(Legs.class);

  }

  /*
  public static void main(String[] args) {
    final Dispatcher dispatcher = new Dispatcher("com.petwal.pwweb.web.controller");
    final Server server = new Server(5000, dispatcher);
    server.start();
  }

   */
}