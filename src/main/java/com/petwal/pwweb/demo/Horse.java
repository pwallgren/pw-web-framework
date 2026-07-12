package com.petwal.pwweb.demo;

public class Horse {

  private Legs legs;

  public Horse(final Legs legs) {
    this.legs = legs;
  }

  @Override
  public String toString() {
    return "Horse{" +
        "legs=" + legs +
        '}';
  }
}
