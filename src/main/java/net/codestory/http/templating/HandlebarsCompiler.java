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
import net.codestory.http.templating.helpers.EachReverseHelper;

import java.io.IOException;
import java.util.Map;

public class HandlebarsCompiler {
	public String compile(String template, Map<String, Object> variables) throws IOException {
		return compile(template, null, variables);
	}

	String compile(String template, Site site, Map<String, Object> variables) throws IOException {
		Handlebars handlebars = createHandlebars(site, variables);

		Context context;
		if (site == null) {
			context = context(null, variables).build();
		} else {
			Context contextSite = context(null, null).combine("site", site).build();
			Context contextYaml = context(contextSite, null).combine("site", site.configYaml()).build();

			context = context(contextYaml, variables).build();
		}

		return handlebars.compileInline(template, "[[", "]]").apply(context);
	}

	private static Handlebars createHandlebars(Site site, Map<String, Object> variables) {
		Handlebars handlebars = new Handlebars(new AbstractTemplateLoader() {
			@Override
			public TemplateSource sourceAt(String location) {
				return new StringTemplateSource(location, new Template("_includes/" + location).render(site, variables));
			}
		});
		StringHelpers.register(handlebars);
		handlebars.registerHelper(EachReverseHelper.NAME, EachReverseHelper.INSTANCE);
		return handlebars;
	}

	private static Context.Builder context(Context parent, Object model) {
		Context.Builder builder;
		if (parent == null) {
			builder = Context.newBuilder(model);
		} else {
			builder = Context.newBuilder(parent, model);
		}
		return builder.resolver(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE, FieldValueResolver.INSTANCE);
	}
}
