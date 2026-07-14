package com.petwal.pwweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petwal.pwweb.context.core.BeanContext;
import com.petwal.pwweb.web.core.Dispatcher;
import com.petwal.pwweb.web.core.Server;

public class PwApplication {

  public static final int DEFAULT_PORT = 8080;

  private final BeanContext beanContext;
  private final Server server;

  private PwApplication(final BeanContext beanContext, final Server server) {
    this.beanContext = beanContext;
    this.server = server;
  }

  public static PwApplication run(final Class<?> primarySource, final int port) {
    return builder(primarySource)
        .port(port)
        .build()
        .start();
  }

  public PwApplication start() {
    Runtime.getRuntime().addShutdownHook(new Thread(beanContext::close, "pw-web-shutdown-hook"));
    server.start();
    return this;
  }

  public BeanContext getBeanContext() {
    return beanContext;
  }

  public static Builder builder(final Class<?> primarySource) {
    return new Builder(primarySource);
  }

  public final static class Builder {

    private String basePackage;
    private int port = DEFAULT_PORT;
    private ObjectMapper objectMapper;

    Builder(final Class<?> primarySource) {
      this.basePackage = primarySource.getPackageName();
    }

    public Builder basePackage(final String basePackage) {
      this.basePackage = basePackage;
      return this;
    }

    public Builder port(final int port) {
      this.port = port;
      return this;
    }

    public Builder objectMapper(final ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }

    public PwApplication build() {
      final BeanContext beanContext = new BeanContext();
      beanContext.scan(basePackage);

      final Dispatcher dispatcher = objectMapper != null
          ? new Dispatcher(beanContext, objectMapper)
          : new Dispatcher(beanContext);

      return new PwApplication(beanContext, new Server(port, dispatcher));
    }

  }
}

