package com.petwal.pwweb.web.core.filter;

import com.petwal.pwweb.web.http.HttpRequest;
import java.util.Optional;

@FunctionalInterface
public interface PwAuthenticator {

  Optional<Object> authenticate(final HttpRequest request);

}
