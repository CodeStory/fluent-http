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
package net.codestory.http.misc;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvTest {
  @Test
  public void prod_mode() {
    Env prod = Env.prod();

    assertThat(prod.prodMode()).isTrue();
    assertThat(prod.classPath()).isTrue();
    assertThat(prod.filesystem()).isTrue();
    assertThat(prod.gzip()).isTrue();
    assertThat(prod.workingDir()).isEqualTo(new File("."));
    assertThat(prod.appFolder()).isEqualTo("app");
    assertThat(prod.injectLiveReloadScript()).isFalse();
    assertThat(prod.liveReloadServer()).isFalse();
    assertThat(prod.diskCache()).isTrue();
  }

  @Test
  public void dev_mode() {
    Env dev = Env.dev();

    assertThat(dev.prodMode()).isFalse();
    assertThat(dev.classPath()).isTrue();
    assertThat(dev.filesystem()).isTrue();
    assertThat(dev.gzip()).isFalse();
    assertThat(dev.workingDir()).isEqualTo(new File("."));
    assertThat(dev.appFolder()).isEqualTo("app");
    assertThat(dev.injectLiveReloadScript()).isTrue();
    assertThat(dev.liveReloadServer()).isTrue();
    assertThat(dev.diskCache()).isTrue();
  }

  @Test
  public void with_working_dir() {
    Env env = Env.prod().withWorkingDir(new File("web"));

    assertThat(env.prodMode()).isTrue();
    assertThat(env.classPath()).isTrue();
    assertThat(env.filesystem()).isTrue();
    assertThat(env.gzip()).isTrue();
    assertThat(env.workingDir()).isEqualTo(new File("web"));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isFalse();
    assertThat(env.liveReloadServer()).isFalse();
    assertThat(env.diskCache()).isTrue();
  }

  @Test
  public void without_prod_mode() {
    Env env = Env.prod().withProdMode(false);

    assertThat(env.prodMode()).isFalse();
    assertThat(env.classPath()).isTrue();
    assertThat(env.filesystem()).isTrue();
    assertThat(env.gzip()).isTrue();
    assertThat(env.workingDir()).isEqualTo(new File("."));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isFalse();
    assertThat(env.liveReloadServer()).isFalse();
    assertThat(env.diskCache()).isTrue();
  }

  @Test
  public void without_classpath() {
    Env env = Env.prod().withClassPath(false);

    assertThat(env.prodMode()).isTrue();
    assertThat(env.classPath()).isFalse();
    assertThat(env.filesystem()).isTrue();
    assertThat(env.gzip()).isTrue();
    assertThat(env.workingDir()).isEqualTo(new File("."));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isFalse();
    assertThat(env.liveReloadServer()).isFalse();
    assertThat(env.diskCache()).isTrue();
  }

  @Test
  public void without_filesystem() {
    Env env = Env.prod().withFilesystem(false);

    assertThat(env.prodMode()).isTrue();
    assertThat(env.classPath()).isTrue();
    assertThat(env.filesystem()).isFalse();
    assertThat(env.gzip()).isTrue();
    assertThat(env.workingDir()).isEqualTo(new File("."));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isFalse();
    assertThat(env.liveReloadServer()).isFalse();
    assertThat(env.diskCache()).isTrue();
  }

  @Test
  public void without_gzip() {
    Env env = Env.prod().withGzip(false);

    assertThat(env.prodMode()).isTrue();
    assertThat(env.classPath()).isTrue();
    assertThat(env.filesystem()).isTrue();
    assertThat(env.gzip()).isFalse();
    assertThat(env.workingDir()).isEqualTo(new File("."));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isFalse();
    assertThat(env.liveReloadServer()).isFalse();
    assertThat(env.diskCache()).isTrue();
  }

  @Test
  public void injectLiveReloadScript() {
    Env env = Env.prod().withInjectLiveReloadScript(true);

    assertThat(env.prodMode()).isTrue();
    assertThat(env.classPath()).isTrue();
    assertThat(env.filesystem()).isTrue();
    assertThat(env.gzip()).isTrue();
    assertThat(env.workingDir()).isEqualTo(new File("."));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isTrue();
    assertThat(env.liveReloadServer()).isFalse();
  }

  @Test
  public void liveReloadServer() {
    Env env = Env.prod().withLiveReloadServer(true);

    assertThat(env.prodMode()).isTrue();
    assertThat(env.classPath()).isTrue();
    assertThat(env.filesystem()).isTrue();
    assertThat(env.gzip()).isTrue();
    assertThat(env.workingDir()).isEqualTo(new File("."));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isFalse();
    assertThat(env.liveReloadServer()).isTrue();
    assertThat(env.diskCache()).isTrue();
  }

  @Test
  public void diskCache() {
    Env env = Env.prod().withDiskCache(false);

    assertThat(env.prodMode()).isTrue();
    assertThat(env.classPath()).isTrue();
    assertThat(env.filesystem()).isTrue();
    assertThat(env.gzip()).isTrue();
    assertThat(env.workingDir()).isEqualTo(new File("."));
    assertThat(env.appFolder()).isEqualTo("app");
    assertThat(env.injectLiveReloadScript()).isFalse();
    assertThat(env.liveReloadServer()).isFalse();
    assertThat(env.diskCache()).isFalse();
  }
}