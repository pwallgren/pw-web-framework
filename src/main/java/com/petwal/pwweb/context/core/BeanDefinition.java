package com.petwal.pwweb.context.core;

import com.petwal.pwweb.web.util.Check;
import java.lang.reflect.Method;

public class BeanDefinition {

  private final Method method;
  private final String name;

  private BeanDefinition(final Method method, final String name) {
    this.method = Check.notNull("method", method);
    this.name = name;
  }

  public static BeanDefinition of(final Method method, final String qualifier) {
    return new BeanDefinition(method, qualifier);
  }

  public Method getMethod() {
    return method;
  }

  public String getName() {
    return name;
  }

}
