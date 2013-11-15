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

public class ModelAndView {
  private final String view;
  private final Model model;

  private ModelAndView(String view, Model model) {
    this.view = view;
    this.model = model;
  }

  public static ModelAndView of(String view) {
    return new ModelAndView(view, Model.of());
  }

  public static ModelAndView of(String view, String key, Object value) {
    return new ModelAndView(view, Model.of(key, value));
  }

  public static ModelAndView of(String view, String k1, Object v1, String k2, Object v2) {
    return new ModelAndView(view, Model.of(k1, v1, k2, v2));
  }

  public static ModelAndView of(String view, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    return new ModelAndView(view, Model.of(k1, v1, k2, v2, k3, v3));
  }

  public static ModelAndView of(String view, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    return new ModelAndView(view, Model.of(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  public String view() {
    return view;
  }

  public Model model() {
    return model;
  }
}
