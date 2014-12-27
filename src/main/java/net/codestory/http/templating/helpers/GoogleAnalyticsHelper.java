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

    return new SafeString("<script type=\"text/javascript\">\n" +
      "  var _gaq = _gaq || [];\n" +
      "  _gaq.push(['_setAccount', '" + escapeExpression(trackingId) + "']);\n" +
      "  _gaq.push(['_trackPageview']);\n" +
      "  (function() {\n" +
      "    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" +
      "    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" +
      "    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" +
      "  })();\n" +
      "</script>");
  }
}
