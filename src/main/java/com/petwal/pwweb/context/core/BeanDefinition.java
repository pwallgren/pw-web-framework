package com.petwal.pwweb.context.core;

import com.petwal.pwweb.util.Check;
import java.lang.reflect.Parameter;
import java.util.List;

public class BeanDefinition {

  private final String name;
  private final List<Parameter> dependencies;
  private final BeanInstantiator instantiator;

  private BeanDefinition(final String name, final List<Parameter> dependencies,
      final BeanInstantiator instantiator) {
    this.name = Check.notNull("name", name);
    this.dependencies = Check.notNull("dependencies", dependencies);
    this.instantiator = Check.notNull("instantiator", instantiator);
  }

  public static BeanDefinition of(final String name, final Parameter[] dependencies,
      final BeanInstantiator instantiator) {
    return new BeanDefinition(name, List.of(dependencies), instantiator);
  }

  public String getName() {
    return name;
  }

  public List<Parameter> getDependencies() {
    return dependencies;
  }

  public Object instantiate(final Object[] args) throws ReflectiveOperationException {
    return instantiator.instantiate(args);
  }

}
