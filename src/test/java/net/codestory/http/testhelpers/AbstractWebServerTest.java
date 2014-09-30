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
package net.codestory.http.testhelpers;

import static com.squareup.okhttp.Request.*;
import static java.net.CookiePolicy.*;
import static net.codestory.http.misc.Fluent.*;
import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.codestory.http.convert.*;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.*;

public abstract class AbstractWebServerTest {

  // GET
  protected RestAssert get(String path) {
    return new RestAssert(path, request -> request);
  }

  protected RestAssert getWithHeader(String path, String name, String value) {
    return new RestAssert(path, request -> request.addHeader(name, value));
  }

  protected RestAssert getWithAuth(String path, String login, String password) {
    AtomicInteger tries = new AtomicInteger(0);

    return new RestAssert(path, client -> client.setAuthenticator(new Authenticator() {
      @Override
      public Request authenticate(Proxy proxy, Response response) throws IOException {
        if (tries.getAndIncrement() > 0) {
          return null;
        }
        String credential = Credentials.basic(login, password);
        return response.request().newBuilder().header("Authorization", credential).build();
      }

      @Override
      public Request authenticateProxy(Proxy proxy, Response response) {
        return null;
      }
    }), request -> request);
  }

  protected RestAssert getWithPreemptiveAuth(String path, String login, String password) {
    return getWithHeader(path, "Authorization", Credentials.basic(login, password));
  }

  // PUT
  protected RestAssert put(String path) {
    return new RestAssert(path, request -> request.put(null));
  }

  protected RestAssert put(String path, String body) {
    return new RestAssert(path, request -> request.put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)));
  }

  // HEAD
  protected RestAssert head(String path) {
    return new RestAssert(path, request -> request.head());
  }

  // OPTIONS
  protected RestAssert options(String path) {
    return new RestAssert(path, request -> request.method("OPTIONS", null).header("Origin", "http://www.code-story.net"));
  }

  protected RestAssert optionsWithHeader(String path, String name, String value) {
    return new RestAssert(path, request -> request.method("OPTIONS", null).header("Origin", "http://www.code-story.net").header(name, value));
  }

  // POST
  protected RestAssert post(String path) {
    return new RestAssert(path, request -> request.post(null));
  }

  protected RestAssert post(String path, String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
    FormEncodingBuilder form = new FormEncodingBuilder();
    form.add(firstParameterName, firstParameterValue.toString());
    for (int i = 0; i < parameterNameValuePairs.length; i += 2) {
      form.add(parameterNameValuePairs[i].toString(), parameterNameValuePairs[i + 1].toString());
    }

    return new RestAssert(path, request -> request.post(form.build()));
  }

  protected RestAssert post(String path, String body) {
    return new RestAssert(path, request -> request.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)));
  }

  // DELETE
  protected RestAssert delete(String path) {
    return new RestAssert(path, request -> request.delete());
  }

  public class RestAssert {
    private final Response response;
    private final CookieManager cookieManager;

    private RestAssert(String path, Function<OkHttpClient, OkHttpClient> configClient, Function<Builder, Builder> configRequest) {
      cookieManager = new CookieManager();
      cookieManager.setCookiePolicy(ACCEPT_ALL);

      try {
        OkHttpClient client = configClient.apply(new OkHttpClient().setCookieHandler(cookieManager));
        Request request = configRequest.apply(new Builder().url("http://localhost:" + getPort() + path)).build();
        response = client.newCall(request).execute();
      } catch (IOException e) {
        throw new RuntimeException("Unable to query", e);
      }
    }

    private RestAssert(String path, Function<Builder, Builder> configRequest) {
      this(path, client -> client, configRequest);
    }

    // Assertions
    public RestAssert produces(String content) {
      try {
        assertThat(response.body().string()).contains(content);
        return this;
      } catch (IOException e) {
        throw new RuntimeException("Unable to read response as String", e);
      }
    }

    public RestAssert produces(String contentType, String content) {
      assertThat(response.header("Content-Type")).contains(contentType);
      return produces(content);
    }

    public RestAssert produces(int code, String contentType, String content) {
      produces(contentType, content);
      produces(code);
      return this;
    }

    public RestAssert produces(int code) {
      assertThat(response.code()).isEqualTo(code);
      return this;
    }

    public RestAssert producesCookie(String name, String value) {
      List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
      String actualValue = of(cookies).firstMatch(cookie -> cookie.getName().equals(name)).map(cookie -> cookie.getValue()).orElse(null);
      assertThat(actualValue).isEqualTo(value);
      return this;
    }

    public <T> RestAssert producesCookie(String name, Class<T> type, Consumer<T> validation) {
      List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
      String actualValue = of(cookies).firstMatch(cookie -> cookie.getName().equals(name)).map(cookie -> cookie.getValue()).orElse(null);
      System.out.println(actualValue);
      validation.accept(TypeConvert.fromJson(actualValue, type));
      return this;
    }

    public RestAssert producesHeader(String name, String value) {
      assertThat(response.header(name)).isEqualTo(value);
      return this;
    }
  }

  protected abstract int getPort();
}