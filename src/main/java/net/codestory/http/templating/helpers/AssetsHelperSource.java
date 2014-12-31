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
package net.codestory.http.templating.helpers;

import com.github.jknack.handlebars.Handlebars.SafeString;
import net.codestory.http.compilers.CacheEntry;
import net.codestory.http.compilers.Compilers;
import net.codestory.http.compilers.SourceFile;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Sha1;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.codestory.http.io.Strings.extension;
import static net.codestory.http.io.Strings.replaceLast;

public class AssetsHelperSource {
  private final Resources resources;
  private final Compilers compilers;

  public AssetsHelperSource(Resources resources, Compilers compilers) {
    this.resources = resources;
    this.compilers = compilers;
  }

  public CharSequence script(Object context) throws IOException {
    String uri = addExtensionIfMissing(context.toString(), ".js");

    return new SafeString("<script src=\"" + uriWithSha1(uri) + "\"></script>");
  }

  public CharSequence css(Object context) throws IOException {
    String uri = addExtensionIfMissing(context.toString(), ".css");

    return new SafeString("<link rel=\"stylesheet\" href=\"" + uriWithSha1(uri) + "\">");
  }

  private String uriWithSha1(String uri) throws IOException {
    Path path = findCompilableTo(uri);
    return (path == null) ? uri : uri + '?' + sha1(path);
  }

  private static String addExtensionIfMissing(String uri, String extension) {
    return uri.endsWith(extension) ? uri : uri + extension;
  }

  private String sha1(Path path) throws IOException {
    CacheEntry compile = compilers.compile(resources.sourceFile(path));
    return Sha1.of(compile.toBytes());
  }

  private Path findCompilableTo(String uri) {
    String extension = extension(uri);

    for (String sourceExtension : compilers.extensionsThatCompileTo(extension)) {
      Path sourcePath = Paths.get(replaceLast(uri, extension, sourceExtension));

      if (resources.isPublic(sourcePath)) {
        return sourcePath;
      }
    }

    return null;
  }
}
