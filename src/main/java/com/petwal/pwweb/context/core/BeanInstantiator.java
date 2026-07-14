package com.petwal.pwweb.context.core;

@FunctionalInterface
public interface BeanInstantiator {

  Object instantiate(Object[] args) throws ReflectiveOperationException;

}
