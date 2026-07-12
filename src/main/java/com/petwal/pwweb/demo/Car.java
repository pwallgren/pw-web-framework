package com.petwal.pwweb.demo;

public class Car {

  private Wheel wheel;

  public Car(final Wheel wheel) {
    this.wheel = wheel;
  }

  @Override
  public String toString() {
    return "Horse{" +
        "legs=" + wheel +
        '}';
  }
}
