package com.petwal.pwweb.context.core;

import com.petwal.pwweb.context.annotation.PwNamed;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanContext {

  private final Map<String, Object> beans;
  private final Map<Class<?>, Object> configInstances;

  public BeanContext() {
    beans = new HashMap<>();
    configInstances = new HashMap<>();
  }

  public <T> T getBean(final String qualifier) {
    return (T) beans.get(qualifier);
  }

  public <T> T getBean(final Class<T> clazz) {
    return clazz.cast(beans.get(clazz.getName()));
  }

  public List<Object> getBeansByAnnotation(final Class<? extends Annotation> annotation) {
    return beans.values()
        .stream()
        .filter(a -> a.getClass().isAnnotationPresent(annotation))
        .toList();
  }

  public void registerBeans(final List<BeanDefinition> beanDefinitions) {
    beanDefinitions
        .forEach(definition -> resolve(definition, beanDefinitions));
  }

  public void scan(final String basePackage) {
    final List<BeanDefinition> beanDefinitions = BeanScanner.scan(basePackage);
    registerBeans(beanDefinitions);
  }

  private Object resolve(final BeanDefinition beanDefinition,
      final List<BeanDefinition> beanDefinitions) {

    final Method method = beanDefinition.getMethod();
    final String key = beanDefinition.getName();
    if (beans.containsKey(key)) {
      return beans.get(key);
    }

    final Parameter[] parameters = method.getParameters();
    final Object[] args = new Object[parameters.length];

    for (int i = 0; i < parameters.length; i++) {
      final Parameter parameter = parameters[i];
      final PwNamed named = parameter.getAnnotation(PwNamed.class);

      final BeanDefinition definition = named != null
          ? findBeanDefinition(named.name(), beanDefinitions)
          : findBeanDefinition(parameter.getType().getName(), beanDefinitions);

      args[i] = resolve(definition, beanDefinitions);
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

  private BeanDefinition findBeanDefinition(final String name,
      final List<BeanDefinition> beanDefinitions) {
    return beanDefinitions.stream()
        .filter(beanDefinition -> beanDefinition.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No bean found for: " + name));
  }

}
