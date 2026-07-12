package com.petwal.pwweb.demo;

import com.petwal.pwweb.context.annotation.PwBean;
import com.petwal.pwweb.context.annotation.PwConfiguration;
import com.petwal.pwweb.context.annotation.PwNamed;

@PwConfiguration
public class ConfigurationTest {

  @PwBean
  public Car car(final Wheel wheel) {
    return new Car(wheel);
  }

  @PwBean(name = "janneCar")
  public Car jannecar(@PwNamed(name = "janneWheel") final Wheel wheel) {
    return new Car(wheel);
  }

  @PwBean(name = "janneWheel")
  public Wheel wheel() {
    return new Wheel("janne wheels");
  }

  @PwBean
  public Wheel jannewheel() {
    return new Wheel("default wheels");
  }

}
