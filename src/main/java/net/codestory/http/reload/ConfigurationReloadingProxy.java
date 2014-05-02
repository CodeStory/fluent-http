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
package net.codestory.http.reload;

import net.codestory.http.*;
import net.codestory.http.io.*;
import net.codestory.http.routes.*;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class ConfigurationReloadingProxy implements Configuration {


  private final ClassLoader parent;
  private final String fqcn;

  public ConfigurationReloadingProxy(Class<? extends Configuration> configuration) {
    fqcn = configuration.getName();
    parent = getClass().getClassLoader();
  }

  public static final <T> T createInstance(Class<T> clazz) {
    try {
      return (T) clazz.newInstance();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to instanciate " + clazz.getName(), e);
    }
  }


  @Override
  public void configure(Routes routes) {
    try {
      ClassLoader cl = new ParentLastClassLoader(Resources.CLASSES_OUTPUT_DIR, parent);

      Configuration delegate = createInstance((Class<Configuration>) cl.loadClass(fqcn));
      delegate.configure(routes);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to reload Configuration from classpath", e);
    }
  }
}
