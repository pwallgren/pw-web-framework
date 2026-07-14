package com.petwal.pwweb.context.core;

import com.petwal.pwweb.util.Check;
import java.lang.reflect.Parameter;
import java.util.List;

public class BeanDefinition {

  public static final BeanLifecycleProcedure NO_OP = (Object instance) -> {
  };
  private final String name;
  private final List<Parameter> dependencies;
  private final BeanInstantiator instantiator;
  private final BeanLifecycleProcedure postConstruct;
  private final BeanLifecycleProcedure preDestroy;

  private BeanDefinition(final Builder builder) {
    name = Check.notNull("name", builder.name);
    dependencies = Check.notNull("dependencies", builder.dependencies);
    instantiator = Check.notNull("instantiator", builder.instantiator);
    postConstruct = builder.postConstruct != null ? builder.postConstruct : NO_OP;
    preDestroy = builder.preDestroy != null ? builder.preDestroy : NO_OP;
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

  public BeanInstantiator getInstantiator() {
    return instantiator;
  }

  public BeanLifecycleProcedure getPostConstruct() {
    return postConstruct;
  }

  public BeanLifecycleProcedure getPreDestroy() {
    return preDestroy;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String name;
    private List<Parameter> dependencies;
    private BeanInstantiator instantiator;
    private BeanLifecycleProcedure postConstruct;
    private BeanLifecycleProcedure preDestroy;

    private Builder() {
    }

    public Builder name(final String val) {
      name = val;
      return this;
    }

    public Builder dependencies(final List<Parameter> val) {
      dependencies = val;
      return this;
    }

    public Builder instantiator(final BeanInstantiator val) {
      instantiator = val;
      return this;
    }

    public Builder postConstruct(final BeanLifecycleProcedure val) {
      postConstruct = val;
      return this;
    }

    public Builder preDestroy(final BeanLifecycleProcedure val) {
      preDestroy = val;
      return this;
    }

    public BeanDefinition build() {
      return new BeanDefinition(this);
    }
  }
}
