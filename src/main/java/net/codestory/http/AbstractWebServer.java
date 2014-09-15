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
import net.codestory.http.errors.*;
import net.codestory.http.misc.*;
import net.codestory.http.payload.*;
import net.codestory.http.reload.*;
import net.codestory.http.routes.*;
import net.codestory.http.templating.*;

import org.slf4j.*;

public abstract class AbstractWebServer {
  protected final static Logger LOG = LoggerFactory.getLogger(AbstractWebServer.class);

  protected RoutesProvider routesProvider;

  public AbstractWebServer configure(Configuration configuration) {
    this.routesProvider = Env.get().prodMode()
      ? RoutesProvider.fixed(configuration)
      : RoutesProvider.reloading(configuration);
    return this;
  }

  protected void handle(Request request, Response response) {
    try {
      RouteCollection routes = routesProvider.get();

      Context context = routes.createContext(request, response);
      PayloadWriter payloadWriter = routes.createPayloadWriter(request, response);

      Payload payload = routes.apply(context);
      if (payload.isError()) {
        payload = errorPage(payload);
      }

      payloadWriter.writeAndClose(payload);
    } catch (Exception e) {
      // Cannot be created by routes since it was not initialized properly
      // TODO: get rid of new Site() here
      //
      PayloadWriter payloadWriter = new PayloadWriter(new Site(), request, response);
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
    Exception shownError = Env.get().prodMode() ? null : e;
    return new ErrorPage(payload, shownError).payload();
  }
}
