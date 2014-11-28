/**
 * Copyright (C) 2013-2014 all@code-story.net
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
package net.codestory.http.extensions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.codestory.http.annotations.Get;
import net.codestory.http.errors.NotFoundException;
import net.codestory.http.misc.Env;
import net.codestory.http.testhelpers.AbstractProdWebServerTest;
import org.junit.Test;

import java.io.IOException;

public class CustomObjectMapperTest extends AbstractProdWebServerTest {
  @Test
  public void add_resolver() {
    server.configure(routes -> routes
      .add(PersonResource.class)
      .setExtensions(new Extensions() {
        @Override
        public ObjectMapper configureOrReplaceObjectMapper(ObjectMapper defaultObjectMapper, Env env) {
          defaultObjectMapper.registerModule(new CustomTypesModule());
          return defaultObjectMapper;
        }
      }));

    get("/person/Bob/town").should().contain("Paris");
    get("/person/John/town").should().contain("NYC");
    get("/person/Jane/town").should().respond(404);
  }

  public static class PersonResource {
    @Get("/person/:name/town")
    public String town(Person forName) {
      return forName.town;
    }
  }

  static class Person {
    String name;
    String town;

    Person(String name, String town) {
      this.name = name;
      this.town = town;
    }
  }

  static class CustomTypesModule extends SimpleModule {
    public CustomTypesModule() {
      addDeserializer(Person.class, new PersonDeserializer());
    }
  }

  static class PersonDeserializer extends JsonDeserializer<Person> {
    @Override
    public Person deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      String name = jp.getValueAsString();

      if (name.equals("Bob")) {
        return new Person("Bob", "Paris");
      } else if (name.equals("John")) {
        return new Person("John", "NYC");
      }
      throw new NotFoundException();
    }
  }
}
