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
package net.codestory.http.templating.helpers;

import static com.github.jknack.handlebars.Handlebars.*;
import static com.github.jknack.handlebars.Handlebars.Utils.*;

import com.github.jknack.handlebars.*;

public class GoogleAnalyticsHelper {
  private final String id;

  public GoogleAnalyticsHelper(String id) {
    this.id = id;
  }

  public GoogleAnalyticsHelper() {
    this.id = null;
  }

  public CharSequence google_analytics(Object context, Options options) {
    String trackingId = (context != null) ? context.toString() : id;
    if (trackingId == null) {
      return "";
    }

    Boolean prodMode = options.get("env.prodMode");
    if ((prodMode != null) && (!prodMode)) {
      return "";
    }

    return new SafeString("<script>\n" +
      "  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n" +
      "  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n" +
      "  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n" +
      "})(window,document,'script','//www.google-analytics.com/analytics.js','ga');\n" +
      "ga('create', '" + escapeExpression(trackingId) + "', 'auto');\n" +
      "ga('send', 'pageview');\n" +
      "</script>");
  }
}
