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
package net.codestory.http;

import net.codestory.http.annotations.*;
import net.codestory.http.testhelpers.*;

import org.junit.*;

public class DeleteTest extends AbstractProdWebServerTest {
  @Test
  public void delete() {
    configure(routes -> routes.delete("/delete", () -> "Deleted"));

    delete("/delete").should().respond(200).haveType("text/html").contain("Deleted");
  }

  @Test
  public void resource() {
    configure(routes -> routes.add(new DeleteResource()));

    delete("/delete").should().respond(200).haveType("text/html").contain("Deleted");
  }

  @Test
  public void resource_class() {
    configure(routes -> routes.add(DeleteResource.class));

    delete("/delete").should().respond(200).haveType("text/html").contain("Deleted");
  }

  public static class DeleteResource {
    @Delete("/delete")
    public String doIt() {
      return "Deleted";
    }
  }
}
