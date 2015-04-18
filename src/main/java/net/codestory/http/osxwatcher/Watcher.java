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
package net.codestory.http.osxwatcher;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class Watcher {
  private static final double LATENCY_S = 0.5;
  private static final int FLAGS = 0x00000002;

  private final WatcherLoop loop;

  public Watcher(File folder, FileChangeListener listener) {
    loop = new WatcherLoop(folder, listener);
  }

  public void start() {
    Thread thread = new Thread(loop);
    thread.setDaemon(true);
    thread.start();

    try {
      loop.started.await();
    } catch (InterruptedException e) {
      // Ignore
    }
  }

  public void stop() {
    loop.stop();
  }

  static class WatcherLoop implements CarbonAPI.FSEventStreamCallback, Runnable {
    private final File folder;
    private final FileChangeListener listener;
    private final CarbonAPI api = CarbonAPI.INSTANCE;
    final CountDownLatch started;

    private PointerByReference stream;
    private PointerByReference runLoop;

    public WatcherLoop(File folder, FileChangeListener listener) {
      this.folder = folder;
      this.listener = listener;
      this.started = new CountDownLatch(1);
    }

    @Override
    public void run() {
      PointerByReference path = api.CFArrayCreate(null, new PointerByReference[]{CarbonAPI.toCFString(folder.getAbsolutePath())}, 1L, null);
      stream = api.FSEventStreamCreate(null, this, null, path, -1, LATENCY_S, FLAGS);
      runLoop = api.CFRunLoopGetCurrent();
      api.FSEventStreamScheduleWithRunLoop(stream, runLoop, CarbonAPI.toCFString("kCFRunLoopDefaultMode"));
      api.FSEventStreamStart(stream);

      started.countDown();
      api.CFRunLoopRun();
    }

    public void stop() {
      api.CFRunLoopStop(runLoop);
      api.FSEventStreamStop(stream);
      api.FSEventStreamInvalidate(stream);
      api.FSEventStreamRelease(stream);
    }

    @Override
    public void invoke(PointerByReference streamRef, Pointer clientCallBackInfo, long numEvents, Pointer eventPaths, Pointer eventFlags, Pointer eventIds) {
      listener.onChange();
    }
  }
}
