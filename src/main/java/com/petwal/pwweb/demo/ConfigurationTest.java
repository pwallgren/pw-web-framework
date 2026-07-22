package com.petwal.pwweb.demo;

import com.petwal.pwweb.context.annotation.PwBean;
import com.petwal.pwweb.context.annotation.PwConfiguration;
import com.petwal.pwweb.context.annotation.PwNamed;
import com.petwal.pwweb.web.core.filter.PwAuthenticator;
import java.util.Optional;

@PwConfiguration
public class ConfigurationTest {

  @PwBean
  public TestService testService() {
    return new TestService("testService OG");
  }

  @PwBean(name = "testService2")
  public TestService testService2() {
    return new TestService("testService 2");
  }

  @PwBean
  public TestController testController(
      final @PwNamed(name = "testService2") TestService testService, final Test test) {
    return new TestController(testService, test);
  }

  @PwBean
  public PwAuthenticator authenticator() {
    return (requestContext) -> {
      // Add authentication code
      // Then return empty if not authenticated, otherwise the principal
      return Optional.of("Authenticated User");
    };
  }

}
