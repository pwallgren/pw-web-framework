package com.petwal.pwweb.context.core;

import com.petwal.pwweb.context.annotation.PwBean;
import com.petwal.pwweb.context.annotation.PwComponent;
import com.petwal.pwweb.context.annotation.PwConfiguration;
import com.petwal.pwweb.context.annotation.PwInject;
import com.petwal.pwweb.context.annotation.PwPostConstruct;
import com.petwal.pwweb.context.annotation.PwPreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
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

          final Class<?> lifecycleTarget = method.getReturnType();
          final BeanLifecycleProcedure postConstruct = resolvePostConstructProcedure(
              lifecycleTarget);
          final BeanLifecycleProcedure preDestroy = resolvePreDestroyProcedure(lifecycleTarget);

          final BeanDefinition beanDefinition = buildBeanDefinition(name, method.getParameters(),
              instantiator, postConstruct, preDestroy);
          beanDefinitions.add(beanDefinition);
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
      final BeanLifecycleProcedure postConstruct = resolvePostConstructProcedure(clazz);
      final BeanLifecycleProcedure preDestroy = resolvePostConstructProcedure(clazz);

      final BeanDefinition beanDefinition = buildBeanDefinition(name, constructor.getParameters(),
          instantiator, postConstruct, preDestroy);
      beanDefinitions.add(beanDefinition);
    }
    return beanDefinitions;
  }

  @Nonnull
  private static BeanDefinition buildBeanDefinition(final String name,
      final Parameter[] parameters,
      final BeanInstantiator instantiator, final BeanLifecycleProcedure postConstruct,
      final BeanLifecycleProcedure preDestroy) {
    return BeanDefinition.builder()
        .name(name)
        .dependencies(Arrays.stream(parameters).toList())
        .instantiator(instantiator)
        .postConstruct(postConstruct)
        .preDestroy(preDestroy)
        .build();
  }

  private static BeanLifecycleProcedure resolvePostConstructProcedure(final Class<?> clazz) {
    return resolveLifecycleProcedure(clazz, PwPostConstruct.class);
  }

  private static BeanLifecycleProcedure resolvePreDestroyProcedure(final Class<?> clazz) {
    return resolveLifecycleProcedure(clazz, PwPreDestroy.class);
  }

  private static BeanLifecycleProcedure resolveLifecycleProcedure(final Class<?> clazz,
      final Class<? extends Annotation> annotation) {

    final List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(annotation))
        .toList();

    if (methods.isEmpty()) {
      return null;
    }
    if (methods.size() > 1) {
      throw new IllegalStateException(
          "More than one @" + annotation.getSimpleName() + " method found");
    }

    final Method method = methods.get(0);
    if (method.getParameterCount() != 0) {
      throw new IllegalStateException(
          "Lifecycle method %s must take no parameters".formatted(method));
    }

    method.setAccessible(true);
    return (final Object instance) -> {
      try {
        method.invoke(instance);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException("Lifecycle method %s failed".formatted(method), e);
      }
    };
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
      throw new RuntimeException("Failed to instantiate configuration class: " + clazz.getName(),
          e);
    }
  }

}
