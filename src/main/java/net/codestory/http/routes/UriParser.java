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
package net.codestory.http.routes;

import net.codestory.http.Query;
import net.codestory.http.io.Strings;

import java.util.Objects;

import static java.util.Arrays.asList;

public class UriParser implements Comparable<UriParser> {
  private final String uriPattern;
  private final String[] patternParts;
  private final String[] queryParamsParts;
  private final int paramsCount;

  public UriParser(String uriPattern) {
    this.uriPattern = uriPattern;
    this.patternParts = parts(stripQueryParams(uriPattern));
    this.queryParamsParts = queryParamsParts(extractQueryParams(uriPattern));
    this.paramsCount = paramsCount(uriPattern);
  }

  public String uriPattern() {
    return uriPattern;
  }

  public String[] params(String uri, Query query) {
    String[] uriParts = parts(uri);
    String[] params = new String[paramsCount];

    int index = 0;
    for (int i = 0; i < patternParts.length; i++) {
      if (patternParts[i].startsWith(":")) {
        if (i == patternParts.length - 1 && patternParts[i].startsWith(":") && patternParts[i].endsWith(":"))
          params[index++] = String.join("/", asList(uriParts).subList(i, uriParts.length));
        else {
          params[index++] = uriParts[i];
        }
      }
    }
    for (int i = 0; i < queryParamsParts.length; i++) {
      if (queryParamsParts[i].startsWith(":")) {
        params[index++] = query.get(queryParamsParts[i - 1]);
      }
    }

    return params;
  }

  public boolean matches(String uri) {
    String[] uriParts = parts(stripQueryParams(uri));
    if (patternParts.length != uriParts.length && !endsWithGreedyParameter(uriPattern)) {
      return false;
    }

    for (int i = 0; i < patternParts.length; i++) {
      if (!patternParts[i].startsWith(":") && !(i < uriParts.length && patternParts[i].equals(uriParts[i]))) {
        return false;
      }
    }

    int lastPart = patternParts.length - 1;
    return lastPart < uriParts.length && !(patternParts[lastPart].startsWith(":") && uriParts[lastPart].isEmpty());
  }

  private static boolean endsWithGreedyParameter(String uriPattern) {
    return uriPattern.lastIndexOf("/") == removeLastChar(uriPattern).lastIndexOf(":") - 1 && uriPattern.endsWith(":");
  }

  private static String removeLastChar(String str) {
    return str.substring(0, str.length() - 1);
  }

  private static String[] parts(String uri) {
    return uri.split("/", -1);
  }

  private static String[] queryParamsParts(String uri) {
    return uri.split("[=&]", -1);
  }

  static String stripQueryParams(String uri) {
    int indexSlash = uri.indexOf('?');
    return (indexSlash == -1) ? uri : uri.substring(0, indexSlash);
  }

  static String extractQueryParams(String uri) {
    int indexSlash = uri.indexOf('?');
    return (indexSlash == -1) ? "" : uri.substring(indexSlash + 1);
  }

  public static int paramsCount(String uriPattern) {
    int nbColons = Strings.countMatches(uriPattern, ':');
    return uriPattern.endsWith(":") ? nbColons - 1:nbColons;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof UriParser) {
      UriParser other = (UriParser) obj;

      return Objects.equals(uriPattern, other.uriPattern);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return uriPattern.hashCode();
  }

  @Override
  public int compareTo(UriParser other) {
    int compare = Integer.compare(paramsCount, other.paramsCount);
    if (compare != 0) {
      return compare;
    }
    int index = 0;
    for (String part : patternParts) {
      boolean hasPart1AColon = part.startsWith(":");
      boolean hasPart2AColon = other.patternParts[index].startsWith(":");
      boolean hasPart1ADoubleColon = hasPart1AColon && part.endsWith(":");
      boolean hasPart2ADoubleColon = hasPart2AColon && other.patternParts[index].endsWith(":");
      if (hasPart1AColon && !hasPart2AColon || hasPart1ADoubleColon && !hasPart2ADoubleColon) {
        return 1;
      }
      if (hasPart2AColon && !hasPart1AColon || !hasPart1ADoubleColon && hasPart2ADoubleColon) {
        return -1;
      }
      index++;
      if (other.patternParts.length == index) {
        return 0;
      }
    }
    return 0;
  }
}
