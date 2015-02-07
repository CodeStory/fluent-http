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

import static java.util.Arrays.asList;
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

public class AssetsHelperSourceTest {
  static Env env = Env.prod();
  static Resources resources = new Resources(env);
  static CompilerFacade compilers = new CompilerFacade(env, resources);

  static AssetsHelperSource assetsHelper = new AssetsHelperSource(true, resources, compilers);

  @Test
  public void script() {
    CharSequence script = assetsHelper.script("js/script.js", null);

    assertThat(script.toString()).isEqualTo("<script src=\"js/script.js?1ae2bed766fa2ed618a9b9048ba41fe094d3f117\"></script>");
  }

  @Test
  public void script_without_extension() {
    CharSequence script = assetsHelper.script("js/script", null);

    assertThat(script.toString()).isEqualTo("<script src=\"js/script.js?1ae2bed766fa2ed618a9b9048ba41fe094d3f117\"></script>");
  }

  @Test
  public void script_with_async_tag() {
    CharSequence script = assetsHelper.script("js/script.js", options("async", null));

    assertThat(script.toString()).isEqualTo("<script src=\"js/script.js?1ae2bed766fa2ed618a9b9048ba41fe094d3f117\" async></script>");
  }

  @Test
  public void script_with_defer_tag() {
    CharSequence script = assetsHelper.script("js/script.js", options("defer", null));

    assertThat(script.toString()).isEqualTo("<script src=\"js/script.js?1ae2bed766fa2ed618a9b9048ba41fe094d3f117\" defer></script>");
  }

  @Test
  public void coffee_script() {
    CharSequence script = assetsHelper.script("js/anotherscript", null);

    assertThat(script.toString()).isEqualTo("<script src=\"js/anotherscript.js?a72b9bd02a15f1307e6f60ac502675a8a24e8581\"></script>");
  }

  @Test
  public void literate_coffee_script() {
    CharSequence script = assetsHelper.script("js/literate", null);

    assertThat(script.toString()).isEqualTo("<script src=\"js/literate.js?186398dc8855a3a68030391d7c81e9aa683d478b\"></script>");
  }

  @Test
  public void unknown_script() {
    CharSequence script = assetsHelper.script("unknown.js", null);

    assertThat(script.toString()).isEqualTo("<script src=\"unknown.js\"></script>");
  }

  @Test
  public void multiple_scripts() {
    CharSequence script = assetsHelper.script(asList("js/script", "js/anotherscript"), null);

    assertThat(script.toString()).isEqualTo(
      "<script src=\"js/script.js?1ae2bed766fa2ed618a9b9048ba41fe094d3f117\"></script>\n" +
        "<script src=\"js/anotherscript.js?a72b9bd02a15f1307e6f60ac502675a8a24e8581\"></script>"
    );
  }

  @Test
  public void css() {
    CharSequence css = assetsHelper.css("assets/style.css", null);

    assertThat(css.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"assets/style.css?80fa881ffa6af083a80845467622c6185949a47b\">");
  }

  @Test
  public void css_without_extension() {
    CharSequence css = assetsHelper.css("assets/style", null);

    assertThat(css.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"assets/style.css?80fa881ffa6af083a80845467622c6185949a47b\">");
  }

  @Test
  public void css_with_media_tag() {
    CharSequence css = assetsHelper.css("assets/style.css", options("media", "screen"));

    assertThat(css.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"assets/style.css?80fa881ffa6af083a80845467622c6185949a47b\" media=\"screen\">");
  }

  @Test
  public void less() {
    CharSequence css = assetsHelper.css("assets/anotherstyle", null);

    assertThat(css.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"assets/anotherstyle.css?dcec144afa669dc921a4c9069d4c7d96fe28a833\">");
  }

  @Test
  public void unknown_css() {
    CharSequence script = assetsHelper.css("unknown.css", null);

    assertThat(script.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"unknown.css\">");
  }

  @Test
  public void multiple_css() {
    CharSequence css = assetsHelper.css(asList("assets/style", "assets/anotherstyle"), null);

    assertThat(css.toString()).isEqualTo(
      "<link rel=\"stylesheet\" href=\"assets/style.css?80fa881ffa6af083a80845467622c6185949a47b\">\n" +
        "<link rel=\"stylesheet\" href=\"assets/anotherstyle.css?dcec144afa669dc921a4c9069d4c7d96fe28a833\">"
    );
  }

  static Options options(String key, String value) {
    return new Options.Builder(mock(Handlebars.class), "tag", TagType.SECTION, mock(Context.class), mock(Template.class))
      .setHash(singletonMap(key, value))
      .build();
  }
}