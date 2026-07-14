package com.petwal.pwweb.context.core;

@FunctionalInterface
public interface BeanLifecycleProcedure {

  void run(Object instance);

}
