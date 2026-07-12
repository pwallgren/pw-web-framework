package com.petwal.pwweb.demo;


import com.petwal.pwweb.context.core.BeanRegistry;

public class Main {

  public static void main(String[] args) {
    final BeanRegistry beanRegistry = new BeanRegistry();
    beanRegistry.scan("com.petwal.pwweb");

    final Car car1 = beanRegistry.getBean("janneHorse");
    final Car car2 = beanRegistry.getBean(Car.class);
    final Wheel wheel1 = beanRegistry.getBean("janneLegs");
    final Wheel wheel2 = beanRegistry.getBean(Wheel.class);
  }

  /*
  public static void main(String[] args) {
    final Dispatcher dispatcher = new Dispatcher("com.petwal.pwweb.web.controller");
    final Server server = new Server(5000, dispatcher);
    server.start();
  }

   */
}