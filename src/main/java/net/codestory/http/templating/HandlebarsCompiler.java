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

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;

import java.io.IOException;
import java.util.Map;

public class HandlebarsCompiler {
  public String compile(String template, Map<String, Object> variables) throws IOException {
    return compile(template, null, variables);
  }

  String compile(String template, Site site, Map<String, Object> variables) throws IOException {
    Handlebars handlebars = new Handlebars(new AbstractTemplateLoader() {
      @Override
      public TemplateSource sourceAt(String location) {
        return new StringTemplateSource(location, new Template("_includes/" + location).render(site, variables));
      }
    });
    StringHelpers.register(handlebars);

    Context context = Context
        .newBuilder(site)
        .combine(variables)
        .resolver(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE, FieldValueResolver.INSTANCE)
        .build();

    return handlebars.compileInline(template, "[[", "]]").apply(context);
  }
}
