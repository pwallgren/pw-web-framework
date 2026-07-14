package com.petwal.pwweb.context.core;

import com.petwal.pwweb.context.annotation.PwNamed;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanContext {

  private final Map<String, Object> beans;
  private final Map<String, BeanDefinition> definitions;

  public BeanContext() {
    beans = new LinkedHashMap<>();
    definitions = new LinkedHashMap<>();
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
        .filter(bean -> bean.getClass().isAnnotationPresent(annotation))
        .toList();
  }

  public void registerBeans(final List<BeanDefinition> beanDefinitions) {
    validateUniqueNames(beanDefinitions);
    beanDefinitions
        .forEach(definition -> resolve(definition, beanDefinitions, new ArrayDeque<>()));
  }

  public void scan(final String basePackage) {
    registerBeans(BeanScanner.scan(basePackage));
  }

  public void close() {
    final List<String> names = new ArrayList<>(beans.keySet());
    Collections.reverse(names);
    names.forEach(name -> definitions.get(name)
        .getPreDestroy()
        .run(beans.get(name)));
  }

  private Object resolve(final BeanDefinition beanDefinition,
      final List<BeanDefinition> beanDefinitions, final Deque<String> resolutionPath) {

    final String key = beanDefinition.getName();
    if (beans.containsKey(key)) {
      return beans.get(key);
    }

    if (resolutionPath.contains(key)) {
      throw new IllegalStateException(
          "Circular dependency detected: " + String.join(" -> ", resolutionPath) + " -> " + key);
    }

    resolutionPath.push(key);
    try {
      final Object[] args = resolveArguments(beanDefinition, beanDefinitions, resolutionPath);
      final Object bean = beanDefinition.instantiate(args);
      beanDefinition.getPostConstruct().run(bean);
      beans.put(key, bean);
      definitions.put(key, beanDefinition);
      return bean;
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Bean factory threw an exception for '" + key + "'", e.getCause());
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to create bean '" + key + "'", e);
    } finally {
      resolutionPath.pop();
    }
  }

  private Object[] resolveArguments(final BeanDefinition beanDefinition,
      final List<BeanDefinition> beanDefinitions, final Deque<String> resolutionPath) {

    final List<Parameter> dependencies = beanDefinition.getDependencies();
    final Object[] args = new Object[dependencies.size()];

    for (int i = 0; i < dependencies.size(); i++) {
      final Parameter parameter = dependencies.get(i);
      final PwNamed named = parameter.getAnnotation(PwNamed.class);

      final BeanDefinition definition = named != null
          ? findBeanDefinition(named.name(), beanDefinitions)
          : findBeanDefinition(parameter.getType().getName(), beanDefinitions);

      args[i] = resolve(definition, beanDefinitions, resolutionPath);
    }
    return args;
  }

  private BeanDefinition findBeanDefinition(final String name,
      final List<BeanDefinition> beanDefinitions) {
    return beanDefinitions.stream()
        .filter(beanDefinition -> beanDefinition.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No bean found for: " + name));
  }

  private void validateUniqueNames(final List<BeanDefinition> beanDefinitions) {
    final Set<String> namesInBatch = new HashSet<>();

    beanDefinitions.forEach(beanDefinition -> {
      final String name = beanDefinition.getName();
      if (namesInBatch.contains(name)) {
        throw new IllegalStateException("Duplicate bean name: " + name);
      }
      namesInBatch.add(name);
    });
  }

}
