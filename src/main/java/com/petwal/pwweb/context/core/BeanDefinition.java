package com.petwal.pwweb.context.core;

import com.petwal.pwweb.web.util.Check;
import java.lang.reflect.Method;

public class BeanDefinition {

  private final Method method;
  private final String name;

  private BeanDefinition(final Method method, final String name) {
    this.method = Check.notNull("method", method);
    this.name = Check.notNull("name", name);
  }

  public static BeanDefinition of(final Method method, final String name) {
    return new BeanDefinition(method, name);
  }

  public Method getMethod() {
    return method;
  }

  public String getName() {
    return name;
  }

}
