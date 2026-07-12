package com.petwal.pwweb.context.core;

import com.petwal.pwweb.context.annotation.PwBean;
import com.petwal.pwweb.context.annotation.PwConfiguration;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

public class BeanScanner {

  public static List<BeanDefinition> scan(final String configurationPath) {

    final List<BeanDefinition> beanDefinitions = new ArrayList<>();

    final Reflections reflections = new Reflections(configurationPath);

    final Set<Class<?>> configurationClasses = reflections.getTypesAnnotatedWith(
        PwConfiguration.class);

    for (Class<?> clazz : configurationClasses) {
      final Method[] methods = clazz.getDeclaredMethods();
      for (Method method : methods) {
        final PwBean pwBean = method.getAnnotation(PwBean.class);
        if (pwBean != null) {
          final String pwBeanName = pwBean.name();
          final String name =
              pwBeanName.isEmpty() ? method.getReturnType().getName() : pwBeanName;
          beanDefinitions.add(BeanDefinition.of(method, name));
        }
      }
    }
    return beanDefinitions;
  }

}
