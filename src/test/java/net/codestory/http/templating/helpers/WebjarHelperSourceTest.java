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

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;

import org.junit.Test;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

public class WebjarHelperSourceTest {
  static Env env = Env.prod();
  static Resources resources = new Resources(env);
  static CompilerFacade compilers = new CompilerFacade(env, resources);

  static WebjarHelperSource webjarHelper = new WebjarHelperSource(true);

  @Test
  public void css() {
    CharSequence script = webjarHelper.webjar("bootstrap.css", null);

    assertThat(script.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"/webjars/bootstrap/3.3.4/css/bootstrap.min.css\">");
  }

  @Test
  public void script() {
    CharSequence script = webjarHelper.webjar("angular.js", null);

    assertThat(script.toString()).isEqualTo("<script src=\"/webjars/angularjs/1.3.14/angular.min.js\"></script>");
  }

  @Test
  public void script_with_defer_tag() {
    CharSequence script = webjarHelper.webjar("angular.js", options("defer", null));

    assertThat(script.toString()).isEqualTo("<script src=\"/webjars/angularjs/1.3.14/angular.min.js\" defer></script>");
  }

  @Test
  public void script_with_async_tag() {
    CharSequence script = webjarHelper.webjar("angular.js", options("async", null));

    assertThat(script.toString()).isEqualTo("<script src=\"/webjars/angularjs/1.3.14/angular.min.js\" async></script>");
  }

  @Test
  public void unknown_css() {
    CharSequence script = webjarHelper.webjar("unknown.css", null);

    assertThat(script.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"unknown.css\">");
  }

  @Test
  public void unknown_script() {
    CharSequence script = webjarHelper.webjar("unknown.js", null);

    assertThat(script.toString()).isEqualTo("<script src=\"unknown.js\"></script>");
  }

  static Options options(String key, String value) {
    return new Options.Builder(mock(Handlebars.class), "tag", TagType.SECTION, mock(Context.class), mock(Template.class))
      .setHash(singletonMap(key, value))
      .build();
  }
}