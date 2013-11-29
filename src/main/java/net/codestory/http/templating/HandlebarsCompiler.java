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

import java.io.*;
import java.util.*;

import net.codestory.http.templating.helpers.*;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.cache.*;
import com.github.jknack.handlebars.context.*;
import com.github.jknack.handlebars.helper.*;
import com.github.jknack.handlebars.io.*;

public class HandlebarsCompiler {
  public String compile(String template, Map<String, Object> variables) throws IOException {
    return handlebars(variables)
        .compileInline(template)
        .apply(context(variables));
  }

  private static Handlebars handlebars(Map<String, Object> variables) {
    return new Handlebars()
        .startDelimiter("[[")
        .endDelimiter("]]")
        .registerHelpers(new EachReverseHelperSource())
        .registerHelpers(new EachValueHelperSource())
        .registerHelpers(StringHelpers.class)
        .registerHelpers(findHandleBarHelper())
        .with(new ConcurrentMapTemplateCache())
        .with(new AbstractTemplateLoader() {
          @Override
          public TemplateSource sourceAt(String location) {
            return new StringTemplateSource(location, new Template("_includes", location).render(variables));
          }
        });
  }

    private static Object findHandleBarHelper() {
        try {
            String handleBarHelperClassName = (String) Site.get().get("handleBarHelper");
            System.out.println("Loading external HandleBar helper class " + handleBarHelperClassName);
            return Class.forName(handleBarHelperClassName).newInstance();
        } catch (Exception e) {
            return NopRegister.class;
        }
    }

    private static Context context(Map<String, Object> variables) {
    return Context.newBuilder(null)
        .resolver(
            MapValueResolver.INSTANCE,
            JavaBeanValueResolver.INSTANCE,
            FieldValueResolver.INSTANCE,
            Site.SiteValueResolver.INSTANCE)
        .combine("site", Site.get())
        .combine(variables)
        .build();
  }

    private static class NopRegister {
    }
}
