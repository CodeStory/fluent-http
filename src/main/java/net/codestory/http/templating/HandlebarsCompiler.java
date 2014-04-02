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
package net.codestory.http.templating;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Arrays.*;

import java.io.*;
import java.util.ArrayList;
import java.util.*;

import net.codestory.http.io.*;
import net.codestory.http.templating.helpers.*;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.cache.*;
import com.github.jknack.handlebars.context.*;
import com.github.jknack.handlebars.helper.*;
import com.github.jknack.handlebars.io.*;

public enum HandlebarsCompiler {
  INSTANCE;

  private final Handlebars handlebars;
  private final List<ValueResolver> resolvers;

  HandlebarsCompiler() {
    this.handlebars = handlebars();
    this.resolvers = new ArrayList<>(asList(
        MapValueResolver.INSTANCE,
        JavaBeanValueResolver.INSTANCE,
        FieldValueResolver.INSTANCE,
        MethodValueResolver.INSTANCE,
        Site.SiteValueResolver.INSTANCE
    ));
  }

  public String compile(String template, Map<String, ?> variables) throws IOException {
    return handlebars.compileInline(template).apply(context(variables));
  }

  private static Handlebars handlebars() {
    Handlebars hb = new Handlebars();

    hb.startDelimiter("[[");
    hb.endDelimiter("]]");
    hb.registerHelpers(new EachReverseHelperSource());
    hb.registerHelpers(new EachValueHelperSource());
    hb.registerHelpers(StringHelpers.class);
    hb.with(new ConcurrentMapTemplateCache());
    hb.with(new AbstractTemplateLoader() {
      @Override
      public TemplateSource sourceAt(String location) throws IOException {
        return new StringTemplateSource(location, Resources.read(Resources.findExistingPath("_includes", location), UTF_8));
      }
    });

    if (isThereACustomHandleBarHelperToLoad()) {
      hb.registerHelpers(findHandleBarHelpers());
    }

    return hb;
  }

  private static boolean isThereACustomHandleBarHelperToLoad() {
    return null != Site.get().get("handleBarHelper");
  }

  private static Class<?> findHandleBarHelpers() {
    String helperClassName = (String) Site.get().get("handleBarHelper");
    try {
      return Class.forName(helperClassName);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to register " + helperClassName);
    }
  }

  private Context context(Map<String, ?> variables) {
    return Context.newBuilder(null)
        .resolver(resolvers.toArray(new ValueResolver[resolvers.size()]))
        .combine("site", Site.get())
        .combine(variables)
        .build();
  }

  public void addResolver(ValueResolver resolver) {
    resolvers.add(resolver);
  }

  private static class NopRegister {
  }
}
