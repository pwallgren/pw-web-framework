package com.petwal.pwweb.web.core.filter;

import java.util.List;

public class FilterChainBuilder {

  private FilterChainBuilder() {
  }

  public static FilterChain build(final List<PwFilter> filters, final FilterChain terminal) {
    FilterChain chain = terminal;
    for (int i = filters.size() - 1; i >= 0; i--) {
      final PwFilter filter = filters.get(i);
      final FilterChain next = chain;
      chain = request -> filter.intercept(request, next);
    }
    return chain;
  }

}
