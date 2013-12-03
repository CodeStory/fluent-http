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

import static org.hamcrest.Matchers.*;

import java.util.function.*;

import com.jayway.restassured.*;
import com.jayway.restassured.response.*;
import com.jayway.restassured.specification.*;

class RestAssert {
  private final ValidatableResponse then;

  private RestAssert(Function<RequestSpecification, RequestSpecification> configuration, Function<RequestSender, Response> action) {
    this.then = action.apply(configuration.apply(RestAssured.given().port(WebServerTest.server.port()))).then();
  }

  private RestAssert(Function<RequestSender, Response> action) {
    this(given -> given, action);
  }

  // GET
  static RestAssert get(String path) {
    return new RestAssert(request -> request.get(path));
  }

  static RestAssert getWithAuth(String path, String login, String password) {
    return new RestAssert(given -> given.auth().preemptive().basic(login, password), request -> request.get(path));
  }

  // PUT
  static RestAssert put(String path) {
    return new RestAssert(request -> request.put(path));
  }

  static RestAssert put(String path, String body) {
    return new RestAssert(given -> given.body(body), request -> request.put(path));
  }

  // POST
  static RestAssert post(String path) {
    return new RestAssert(request -> request.post(path));
  }

  static RestAssert post(String path, String firstParameterName, Object firstParameterValue, Object... parameterNameValuePairs) {
    return new RestAssert((given) -> given.parameters(firstParameterName, firstParameterValue, parameterNameValuePairs), request -> request.post(path));
  }

  static RestAssert post(String path, String body) {
    return new RestAssert((given) -> given.body(body), request -> request.post(path));
  }

  // DELETE
  static RestAssert delete(String path) {
    return new RestAssert((given) -> given, request -> request.delete(path));
  }

  // Assertions
  void produces(String content) {
    then.content(containsString(content));
  }

  void produces(String contentType, String content) {
    then.content(containsString(content)).contentType(contentType);
  }

  void produces(int code, String contentType, String content) {
    then.content(containsString(content)).contentType(contentType).statusCode(code);
  }

  void produces(int code) {
    then.statusCode(code);
  }

  void producesCookie(String name, String value) {
    then.cookie(name, value);
  }

  void producesHeader(String name, String value) {
    then.header(name, value);
  }
}
