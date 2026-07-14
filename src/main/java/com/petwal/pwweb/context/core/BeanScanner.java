package com.petwal.pwweb.context.core;

import com.petwal.pwweb.context.annotation.PwBean;
import com.petwal.pwweb.context.annotation.PwComponent;
import com.petwal.pwweb.context.annotation.PwConfiguration;
import com.petwal.pwweb.context.annotation.PwInject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

public class BeanScanner {

  public static List<BeanDefinition> scan(final String basePackage) {
    final Reflections reflections = new Reflections(basePackage);

    final List<BeanDefinition> beanDefinitions = new ArrayList<>();
    beanDefinitions.addAll(scanConfigurations(reflections));
    beanDefinitions.addAll(scanComponents(reflections));
    return beanDefinitions;
  }

  private static List<BeanDefinition> scanConfigurations(final Reflections reflections) {
    final List<BeanDefinition> beanDefinitions = new ArrayList<>();

    final Set<Class<?>> configurationClasses =
        reflections.getTypesAnnotatedWith(PwConfiguration.class);

    for (final Class<?> clazz : configurationClasses) {
      final Object configInstance = newInstance(clazz);
      for (final Method method : clazz.getDeclaredMethods()) {
        final PwBean pwBean = method.getAnnotation(PwBean.class);
        if (pwBean != null) {
          final String name = pwBean.name().isEmpty()
              ? method.getReturnType().getName()
              : pwBean.name();
          final BeanInstantiator instantiator = args -> method.invoke(configInstance, args);
          beanDefinitions.add(BeanDefinition.of(name, method.getParameters(), instantiator));
        }
      }
    }
    return beanDefinitions;
  }

  private static List<BeanDefinition> scanComponents(final Reflections reflections) {
    final List<BeanDefinition> beanDefinitions = new ArrayList<>();

    final Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(PwComponent.class);

    for (final Class<?> clazz : componentClasses) {
      final PwComponent pwComponent = clazz.getAnnotation(PwComponent.class);
      final String name = pwComponent.name().isEmpty()
          ? clazz.getName()
          : pwComponent.name();

      final Constructor<?> constructor = findInjectableConstructor(clazz);
      final BeanInstantiator instantiator = constructor::newInstance;
      beanDefinitions.add(BeanDefinition.of(name, constructor.getParameters(), instantiator));
    }
    return beanDefinitions;
  }

  private static Constructor<?> findInjectableConstructor(final Class<?> clazz) {
    final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    if (constructors.length == 1) {
      return constructors[0];
    }

    final List<Constructor<?>> annotated = Arrays.stream(constructors)
        .filter(constructor -> constructor.isAnnotationPresent(PwInject.class))
        .toList();

    if (annotated.size() == 1) {
      return annotated.get(0);
    }

    throw new IllegalStateException(
        "Component %s has %d constructors; annotate exactly one with @PwInject to disambiguate"
            .formatted(clazz.getName(), constructors.length));
  }

  private static Object newInstance(final Class<?> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to instantiate configuration class: " + clazz.getName(), e);
    }
  }

}
