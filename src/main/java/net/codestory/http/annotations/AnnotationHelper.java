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
package net.codestory.http.annotations;

import static net.codestory.http.constants.Methods.*;
import static net.codestory.http.misc.ForEach.*;

import java.lang.reflect.*;

import net.codestory.http.misc.*;

public class AnnotationHelper {
  private AnnotationHelper() {
    // static class
  }

  public static void parseAnnotations(String urlPrefix, Class<?> type, MethodAnnotationCallback callbask) {
    // Hack to support Mockito Spies
    if (type.getName().contains("EnhancerByMockito")) {
      type = type.getSuperclass();
    }

    Prefix prefixAnnotation = type.getAnnotation(Prefix.class);
    String classPrefix = (prefixAnnotation != null) ? prefixAnnotation.value() : "";

    for (Method method : type.getMethods()) {
      forEach(method.getAnnotationsByType(Get.class)).then(get -> callbask.onMethod(GET, url(urlPrefix, classPrefix, get.value()), method));
      forEach(method.getAnnotationsByType(Post.class)).then(post -> callbask.onMethod(POST, url(urlPrefix, classPrefix, post.value()), method));
      forEach(method.getAnnotationsByType(Put.class)).then(put -> callbask.onMethod(PUT, url(urlPrefix, classPrefix, put.value()), method));
      forEach(method.getAnnotationsByType(Delete.class)).then(delete -> callbask.onMethod(DELETE, url(urlPrefix, classPrefix, delete.value()), method));
      forEach(method.getAnnotationsByType(Head.class)).then(head -> callbask.onMethod(HEAD, url(urlPrefix, classPrefix, head.value()), method));
      forEach(method.getAnnotationsByType(Options.class)).then(options -> callbask.onMethod(OPTIONS, url(urlPrefix, classPrefix, options.value()), method));
    }
  }

  static String url(String resourcePrefix, String classPrefix, String uri) {
    return new UrlConcat().url(resourcePrefix, classPrefix, uri);
  }

  public static interface MethodAnnotationCallback {
    void onMethod(String httpMethod, String uri, Method method);
  }
}
