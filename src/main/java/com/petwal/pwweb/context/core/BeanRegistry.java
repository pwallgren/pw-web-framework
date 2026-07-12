package com.petwal.pwweb.context.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanRegistry {

  private final Map<String, Object> beans;
  private final Map<Class<?>, Object> configInstances;

  public BeanRegistry() {
    beans = new HashMap<>();
    configInstances = new HashMap<>();
  }

  public Object getBean(final String qualifier) {
    return beans.get(qualifier);
  }

  public <T> T getBean(final Class<T> clazz) {
    return clazz.cast(beans.get(clazz.getName()));
  }

  public void registerBeans(final List<BeanDefinition> beanDefinitions) {
    beanDefinitions
        .forEach(definition -> resolve(definition.getMethod(), beanDefinitions));
  }

  public void scan(final String basePackage) {
    final List<BeanDefinition> beanDefinitions = BeanScanner.scan(basePackage);
    registerBeans(beanDefinitions);
  }

  private Object resolve(final Method method, final List<BeanDefinition> beanDefinitions) {

    final String key = method.getReturnType().getName();
    if (beans.containsKey(key)) {
      return beans.get(key);
    }

    final Parameter[] parameters = method.getParameters();
    final Object[] args = new Object[parameters.length];

    for (int i = 0; i < parameters.length; i++) {
      final Method producer = findProducer(parameters[i].getType(), beanDefinitions);
      args[i] = resolve(producer, beanDefinitions);
    }

    try {
      final Object configInstance = configInstance(method.getDeclaringClass());
      final Object object = method.invoke(configInstance, args);
      beans.put(key, object);
      return object;
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Bean factory threw an exception: " + method, e.getCause());
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to invoke bean factory: " + method, e);
    }

  }

  private Object configInstance(final Class<?> configurationClass)
      throws ReflectiveOperationException {
    if (!configInstances.containsKey(configurationClass)) {
      configInstances.put(configurationClass,
          configurationClass.getDeclaredConstructor().newInstance());
    }
    return configInstances.get(configurationClass);
  }

  private Method findProducer(final Class<?> type, final List<BeanDefinition> beanDefinitions) {
    return beanDefinitions.stream()
        .map(BeanDefinition::getMethod)
        .filter(method -> type.isAssignableFrom(method.getReturnType()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No bean found for type: " + type.getName()));
  }

}
