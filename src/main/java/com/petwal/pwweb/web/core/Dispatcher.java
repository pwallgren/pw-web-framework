package com.petwal.pwweb.web.core;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.petwal.pwweb.context.core.BeanContext;
import com.petwal.pwweb.web.core.exceptions.ExceptionHandler;
import com.petwal.pwweb.web.core.exceptions.NotFoundException;
import com.petwal.pwweb.web.core.filter.FilterChain;
import com.petwal.pwweb.web.core.filter.FilterChainBuilder;
import com.petwal.pwweb.web.core.filter.FilterRegistry;
import com.petwal.pwweb.web.core.filter.PwFilter;
import com.petwal.pwweb.web.core.route.RouteEntry;
import com.petwal.pwweb.web.core.route.RouteRegistry;
import com.petwal.pwweb.web.http.HttpRequest;
import com.petwal.pwweb.web.http.HttpRequestParser;
import com.petwal.pwweb.web.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Dispatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

  private final List<RouteEntry> routes;
  private final ResponseWriter responseWriter;
  private final FilterChain filterChain;

  public Dispatcher(final BeanContext beanContext) {
    final ObjectMapper objectMapper = createDefaultMapper();
    this.routes = RouteRegistry.register(beanContext, objectMapper);
    this.responseWriter = new ResponseWriter(objectMapper);
    final List<PwFilter> registeredFilters = FilterRegistry.register(beanContext);
    this.filterChain = FilterChainBuilder.build(registeredFilters, this::dispatch);
  }

  public Dispatcher(final BeanContext beanContext, final ObjectMapper objectMapper) {
    this.routes = RouteRegistry.register(beanContext, objectMapper);
    this.responseWriter = new ResponseWriter(objectMapper);
    final List<PwFilter> registeredFilters = FilterRegistry.register(beanContext);
    this.filterChain = FilterChainBuilder.build(registeredFilters, this::dispatch);
  }

  public void handle(final Socket socket) {
    BufferedReader inputStream = null;
    BufferedWriter outputStream = null;
    try {
      inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      final HttpRequest request = HttpRequestParser.parse(inputStream);
      final HttpResponse response = filterChain.next(request);
      responseWriter.send(response, outputStream);
    } catch (Exception ex) {
      handleExceptions(ex, outputStream);
    } finally {
      closeStreams(inputStream, outputStream, socket);
    }
  }

  private HttpResponse dispatch(final HttpRequest request) {
    try {
      final RouteEntry routeEntry = getMatchingRoute(request);
      return routeEntry.invokeHandler(request);
    } catch (Exception ex) {
      return toErrorResponse(ex);
    }
  }

  public List<RouteEntry> getRoutes() {
    return routes;
  }

  private RouteEntry getMatchingRoute(final HttpRequest request) {
    return routes.stream()
        .filter(entry -> entry.getHttpMethod() == request.getMethod())
        .filter(route -> route.getPattern().match(request.getPath()))
        .findFirst()
        .orElseThrow(() -> new NotFoundException("Handler method for request not found"));
  }

  private HttpResponse toErrorResponse(final Exception exception) {
    LOGGER.error("Exception encountered", exception);
    return ExceptionHandler.handle(exception);
  }

  private void handleExceptions(final Exception exception, final BufferedWriter outputStream) {
    try {
      responseWriter.send(toErrorResponse(exception), outputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void closeStreams(final BufferedReader inputStream,
      final BufferedWriter outputStream, final Socket socket) {
    try {
      if (inputStream != null) {
        inputStream.close();
      }
    } catch (IOException e) {
      LOGGER.error("Failed to close inputStream: {} ", e.getMessage());
    }
    try {
      if (outputStream != null) {
        outputStream.close();
      }
    } catch (IOException e) {
      LOGGER.error("Failed to close outputStream: {} ", e.getMessage());
    }
    try {
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      LOGGER.error("Failed to close socket: {} ", e.getMessage());
    }
  }

  private ObjectMapper createDefaultMapper() {
    return new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
