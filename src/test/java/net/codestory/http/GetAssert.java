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

import com.jayway.restassured.*;
import com.jayway.restassured.specification.*;

class GetAssert {
  private final String path;
  private final ResponseSpecification expect;

  private GetAssert(String path) {
    this.path = path;
    this.expect = RestAssured.given().port(WebServerTest.server.port()).expect();
  }

  static GetAssert get(String path) {
    return new GetAssert(path);
  }

  public void produces(int code, String contentType, String content) {
    expect.content(containsString(content)).contentType(contentType).statusCode(code).when().get(path);
  }

  public void produces(int code) {
    expect.statusCode(code).when().get(path);
  }

  public void produces(String contentType, String content) {
    expect.content(containsString(content)).contentType(contentType).when().get(path);
  }

  public void produces(String content) {
    expect.content(containsString(content)).when().get(path);
  }
}
