package com.petwal.pwweb.web.core.filter;

import com.petwal.pwweb.context.core.BeanContext;
import java.util.Comparator;
import java.util.List;

public class FilterRegistry {

  private FilterRegistry() {
  }

  public static List<PwFilter> register(final BeanContext beanContext) {
    return beanContext.getBeansOfType(PwFilter.class)
        .stream()
        .sorted(Comparator.comparingInt(PwFilter::order))
        .toList();
  }

}
