/**
 * Copyright (C) 2013-2015 all@code-story.net
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
package net.codestory.http.routes;

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.nio.file.*;

import net.codestory.http.testhelpers.*;

import org.junit.*;
import org.junit.rules.*;

public class BoundFolderTest extends AbstractProdWebServerTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Test
  public void bind_static_folder() throws IOException {
    File uploads = temp.newFolder("uploads");
    File special = temp.newFolder("special");
    write(new File(uploads, "doc.txt"), "Doc");
    write(new File(special, "file.html"), "File");
    write(new File(special, "_private.txt"), "Private");
    write(new File(temp.getRoot(), "private.txt"), "Private");

    configure(routes -> routes
        .bind("/uploads", uploads)
        .bind("/special", special)
        .url("/prefix")
        .bind("/special", special));

    get("/uploads/doc.txt").should().contain("Doc").haveType("text/plain;charset=UTF-8");
    get("/special/file.html").should().contain("File").haveType("text/html;charset=UTF-8");
    get("/prefix/special/file.html").should().contain("File").haveType("text/html;charset=UTF-8");
    get("/special/not_found.txt").should().respond(404);
    get("/special/_private.txt").should().respond(404);
    get("/special/../private.txt").should().respond(404);
  }

  static void write(File file, String content) throws IOException {
    Files.write(file.toPath(), content.getBytes(UTF_8));
  }
}
