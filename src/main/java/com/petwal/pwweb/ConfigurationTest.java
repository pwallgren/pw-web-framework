package com.petwal.pwweb;

import com.petwal.pwweb.context.annotation.PwBean;
import com.petwal.pwweb.context.annotation.PwConfiguration;
import com.petwal.pwweb.demo.Horse;
import com.petwal.pwweb.demo.Legs;

@PwConfiguration
public class ConfigurationTest {

  @PwBean
  public Horse horse(final Legs legs) {
    return new Horse(legs);
  }

  @PwBean
  public Legs legs() {
    return new Legs(1);
  }

}
