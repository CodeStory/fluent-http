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

import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetsHelperSourceTest {
  static Env env = new Env();
  static Resources resources = new Resources(env);
  static CompilerFacade compilers = new CompilerFacade(env, resources);

  static AssetsHelperSource assetsHelper = new AssetsHelperSource(true, compilers);

  @Test
  public void script() throws IOException {
    CharSequence script = assetsHelper.script("js/script.js");

    assertThat(script.toString()).isEqualTo("<script src=\"js/script.js?c9810b7d718b08b48eb334341711af6a7dd11474\"></script>");
  }

  @Test
  public void script_without_extension() throws IOException {
    CharSequence script = assetsHelper.script("js/script");

    assertThat(script.toString()).isEqualTo("<script src=\"js/script.js?c9810b7d718b08b48eb334341711af6a7dd11474\"></script>");
  }

  @Test
  public void coffee_script() throws IOException {
    CharSequence script = assetsHelper.script("js/anotherscript");

    assertThat(script.toString()).isEqualTo("<script src=\"js/anotherscript.js?c950010bba0f376d0a970a76a1be245ed4d8779b\"></script>");
  }

  @Test
  public void literate_coffee_script() throws IOException {
    CharSequence script = assetsHelper.script("js/literate");

    assertThat(script.toString()).isEqualTo("<script src=\"js/literate.js?ee26af14f54e1a8c3bb959c644d1c6c456f889c8\"></script>");
  }

  @Test
  public void unknown_script() throws IOException {
    CharSequence script = assetsHelper.script("unknown.js");

    assertThat(script.toString()).isEqualTo("<script src=\"unknown.js\"></script>");
  }

  @Test
  public void multiple_scripts() throws IOException {
    CharSequence script = assetsHelper.script(Arrays.asList("js/script", "js/anotherscript"));

    assertThat(script.toString()).isEqualTo(
        "<script src=\"js/script.js?c9810b7d718b08b48eb334341711af6a7dd11474\"></script>\n" +
        "<script src=\"js/anotherscript.js?c950010bba0f376d0a970a76a1be245ed4d8779b\"></script>"
    );
  }

  @Test
  public void css() throws IOException {
    CharSequence css = assetsHelper.css("assets/style.css");

    assertThat(css.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"assets/style.css?f6b1dd2cbd097b0dbe07138aafd36ab0eafcb6ea\">");
  }

  @Test
  public void css_without_extension() throws IOException {
    CharSequence css = assetsHelper.css("assets/style");

    assertThat(css.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"assets/style.css?f6b1dd2cbd097b0dbe07138aafd36ab0eafcb6ea\">");
  }

  @Test
  public void less() throws IOException {
    CharSequence css = assetsHelper.css("assets/anotherstyle");

    assertThat(css.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"assets/anotherstyle.css?d79d6fc6cd35431fbeb15e192895b038b43d29b0\">");
  }

  @Test
  public void unknown_css() throws IOException {
    CharSequence script = assetsHelper.css("unknown.css");

    assertThat(script.toString()).isEqualTo("<link rel=\"stylesheet\" href=\"unknown.css\">");
  }

  @Test
  public void multiple_css() throws IOException {
    CharSequence css = assetsHelper.css(Arrays.asList("assets/style", "assets/anotherstyle"));

    assertThat(css.toString()).isEqualTo(
      "<link rel=\"stylesheet\" href=\"assets/style.css?f6b1dd2cbd097b0dbe07138aafd36ab0eafcb6ea\">\n" +
      "<link rel=\"stylesheet\" href=\"assets/anotherstyle.css?d79d6fc6cd35431fbeb15e192895b038b43d29b0\">"
    );
  }
}