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
package net.codestory.http.osxwatcher;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface CarbonAPI extends Library {
  CarbonAPI INSTANCE = (CarbonAPI) Native.loadLibrary("Carbon", CarbonAPI.class);

  PointerByReference CFArrayCreate(PointerByReference allocator, Pointer[] values, NativeLong numValues, Void ignore);

  PointerByReference CFStringCreateWithCharacters(Void alloc, char[] chars, NativeLong numChars);

  PointerByReference FSEventStreamCreate(Pointer v, FSEventStreamCallback callback, Pointer context, PointerByReference pathsToWatch, long sinceWhen, double latency, int flags);

  boolean FSEventStreamStart(PointerByReference streamRef);

  void FSEventStreamStop(PointerByReference streamRef);

  void FSEventStreamScheduleWithRunLoop(PointerByReference streamRef, PointerByReference runLoop, PointerByReference runLoopMode);

  PointerByReference CFRunLoopGetCurrent();

  void CFRunLoopRun();

  void CFRunLoopStop(PointerByReference rl);

  static PointerByReference toCFString(String s) {
    final char[] chars = s.toCharArray();
    int length = chars.length;
    return CarbonAPI.INSTANCE.CFStringCreateWithCharacters(null, chars, new NativeLong(length));
  }

  interface FSEventStreamCallback extends Callback {
    void invoke(PointerByReference streamRef, Pointer clientCallBackInfo, NativeLong numEvents, Pointer eventPaths, Pointer eventFlags, Pointer eventIds);
  }
}
