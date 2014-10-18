/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http;

import java.io.*;
import java.util.*;

import net.codestory.http.compilers.*;
import net.codestory.http.convert.*;
import net.codestory.http.errors.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.reload.*;
import net.codestory.http.routes.*;
import net.codestory.http.templating.*;

import org.slf4j.*;

import com.fasterxml.jackson.databind.*;

public abstract class AbstractWebServer {
  protected final static Logger LOG = LoggerFactory.getLogger(AbstractWebServer.class);

  protected final Env env;
  protected final CompilerFacade compilerFacade;
  protected RoutesProvider routesProvider;

  protected AbstractWebServer() {
    this.env = createEnv();
    this.compilerFacade = createCompilerFacade(env);
    TypeConvert.configureMapper(mapper -> configureObjectMapper(mapper));
  }

  public AbstractWebServer configure(Configuration configuration) {
    this.routesProvider = env.prodMode()
        ? RoutesProvider.fixed(env, configuration)
        : RoutesProvider.reloading(env, configuration);
    return this;
  }

  protected void handle(Request request, Response response) {
    try {
      RouteCollection routes = routesProvider.get();

      Context context = createContext(request, response, routes);

      Payload payload = routes.apply(context);
      if (payload.isError()) {
        payload = errorPage(payload);
      }

      PayloadWriter payloadWriter = createPayloadWriter(request, response, env, routes.site(), compilerFacade);
      payloadWriter.writeAndClose(payload);
    } catch (Exception e) {
      PayloadWriter payloadWriter = createPayloadWriter(request, response, env, new Site(env), compilerFacade);
      handleServerError(payloadWriter, e);
    }
  }

  protected void handleServerError(PayloadWriter payloadWriter, Exception e) {
    try {
      if (e instanceof CompilerException) {
        LOG.error(e.getMessage());
      } else if (!(e instanceof HttpException) && !(e instanceof NoSuchElementException)) {
        e.printStackTrace();
      }

      Payload errorPage = errorPage(e).withHeader("reason", e.getMessage());
      payloadWriter.writeAndClose(errorPage);
    } catch (IOException error) {
      LOG.warn("Unable to serve an error page", error);
    }
  }

  protected Payload errorPage(Payload payload) {
    return errorPage(payload, null);
  }

  protected Payload errorPage(Exception e) {
    int code = 500;
    if (e instanceof HttpException) {
      code = ((HttpException) e).code();
    } else if (e instanceof NoSuchElementException) {
      code = 404;
    }

    return errorPage(new Payload(code), e);
  }

  protected Payload errorPage(Payload payload, Exception e) {
    Exception shownError = env.prodMode() ? null : e;
    return new ErrorPage(payload, shownError).payload();
  }

  protected void configureObjectMapper(ObjectMapper objectMapper) {
    // Do nothing by default
  }

  protected Env createEnv() {
    return new Env();
  }

  protected HandlebarsCompiler createHandlebarsCompiler(Compilers compilers) {
    return new HandlebarsCompiler(compilers);
  }

  protected Compilers createCompilers(Env env) {
    return new Compilers(env);
  }

  protected CompilerFacade createCompilerFacade(Env env) {
    Compilers compilers = createCompilers(env);
    HandlebarsCompiler handlebar = createHandlebarsCompiler(compilers);

    return new CompilerFacade(compilers, handlebar);
  }

  protected PayloadWriter createPayloadWriter(Request request, Response response, Env env, Site site, CompilerFacade compilerFacade) {
    return new PayloadWriter(env, site, compilerFacade, request, response);
  }

  protected Context createContext(Request request, Response response, RouteCollection routes) {
    return new Context(request, response, routes.iocAdapter(), routes.site());
  }
}
