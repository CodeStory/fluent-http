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
package net.codestory.http.annotations;

import static java.util.Optional.*;
import static java.util.stream.Stream.of;
import static net.codestory.http.constants.Methods.*;

import java.lang.reflect.*;

import net.codestory.http.misc.*;

public class AnnotationHelper {
  private AnnotationHelper() {
    // static class
  }

  public static void parseAnnotations(String urlPrefix, Class<?> resourceType, MethodAnnotationCallback callback) {
    Class<?> targetType = unwrapIfItsAMockType(resourceType);

    String classPrefix = ofNullable(targetType.getAnnotation(Prefix.class)).map(Prefix::value).orElse("");

    for (Method method : targetType.getMethods()) {
      of(method.getAnnotationsByType(Get.class)).forEach(get -> callback.onMethod(GET, url(urlPrefix, classPrefix, get.value()), method));
      of(method.getAnnotationsByType(Post.class)).forEach(post -> callback.onMethod(POST, url(urlPrefix, classPrefix, post.value()), method));
      of(method.getAnnotationsByType(Put.class)).forEach(put -> callback.onMethod(PUT, url(urlPrefix, classPrefix, put.value()), method));
      of(method.getAnnotationsByType(Delete.class)).forEach(delete -> callback.onMethod(DELETE, url(urlPrefix, classPrefix, delete.value()), method));
      of(method.getAnnotationsByType(Head.class)).forEach(head -> callback.onMethod(HEAD, url(urlPrefix, classPrefix, head.value()), method));
      of(method.getAnnotationsByType(Options.class)).forEach(options -> callback.onMethod(OPTIONS, url(urlPrefix, classPrefix, options.value()), method));
    }
  }

  // Hack to support Mockito Spies
  private static Class<?> unwrapIfItsAMockType(Class<?> type) {
    return type.getName().contains("EnhancerByMockito") ? type.getSuperclass() : type;
  }

  private static String url(String resourcePrefix, String classPrefix, String uri) {
    return new UrlConcat().url(resourcePrefix, classPrefix, uri);
  }

  public interface MethodAnnotationCallback {
    void onMethod(String httpMethod, String uri, Method method);
  }
}
