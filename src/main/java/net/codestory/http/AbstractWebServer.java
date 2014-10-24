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

import net.codestory.http.compilers.CompilerException;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.compilers.Compilers;
import net.codestory.http.errors.ErrorPage;
import net.codestory.http.errors.HttpException;
import net.codestory.http.misc.Env;
import net.codestory.http.misc.NamedDaemonThreadFactory;
import net.codestory.http.payload.Payload;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.reload.RoutesProvider;
import net.codestory.http.routes.RouteCollection;
import net.codestory.http.templating.HandlebarsCompiler;
import net.codestory.http.templating.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractWebServer {
  protected final static Logger LOG = LoggerFactory.getLogger(AbstractWebServer.class);

  protected final Env env;
  protected final CompilerFacade compilers;
  protected final ExecutorService executorService;
  protected RoutesProvider routesProvider;

  protected AbstractWebServer() {
    this.env = createEnv();
    this.compilers = createCompilerFacade();
    this.executorService = createExecutorService();
  }

  protected AbstractWebServer configure(Configuration configuration) {
    this.routesProvider = env.prodMode()
      ? RoutesProvider.fixed(env, compilers, configuration)
      : RoutesProvider.reloading(env, compilers, configuration);
    return this;
  }

  protected void handle(Request request, Response response) {
    try {
      RouteCollection routes = routesProvider.get();

      Context context = routes.createContext(request, response);

      Payload payload = routes.apply(context);
      if (payload.isError()) {
        payload = errorPage(payload);
      }

      PayloadWriter payloadWriter = routes.createPayloadWriter(request, response, executorService);
      payloadWriter.writeAndClose(payload);
    } catch (Exception e) {
      PayloadWriter payloadWriter = new PayloadWriter(request, response, env, new Site(env), compilers, executorService);
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

  protected Env createEnv() {
    return new Env();
  }

  protected CompilerFacade createCompilerFacade() {
    Compilers compilers = new Compilers(env);
    HandlebarsCompiler handlebar = new HandlebarsCompiler(compilers);

    return new CompilerFacade(compilers, handlebar);
  }

  protected ExecutorService createExecutorService() {
    return Executors.newCachedThreadPool(new NamedDaemonThreadFactory());
  }
}
