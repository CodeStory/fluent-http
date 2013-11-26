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
package net.codestory.http.misc;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.stream.*;

import org.junit.*;

public class FluentTest {
  @Test
  public void create_empty() {
    Iterable<String> empty = Fluent.of();

    assertThat(empty).isEmpty();
  }

  @Test
  public void create_for_array() {
    Iterable<String> values = Fluent.of("FIRST", "SECOND");

    assertThat(values).containsExactly("FIRST", "SECOND");
  }

  @Test
  public void create_for_iterable() {
    Iterable<String> values = Fluent.of(Arrays.asList("FIRST", "SECOND"));

    assertThat(values).containsExactly("FIRST", "SECOND");
  }

  @Test
  public void create_for_self() {
    Fluent<String> fluent = Fluent.of(Arrays.asList("FIRST", "SECOND"));
    Iterable<String> values = Fluent.of(fluent);

    assertThat((Object) values).isSameAs(fluent);
  }

  @Test
  public void create_for_iterator() {
    Fluent<String> fluent = Fluent.of(Arrays.asList("FIRST", "SECOND").iterator());

    assertThat(fluent.toList()).containsExactly("FIRST", "SECOND");
    assertThat(fluent.toList()).isEmpty();
  }

  @Test
  public void create_for_stream() {
    Fluent<String> fluent = Fluent.of(Stream.of("FIRST", "SECOND"));

    assertThat(fluent.toList()).containsExactly("FIRST", "SECOND");
  }

  @Test
  public void create_for_int_array() {
    int[] ints = {1, 2, 3, 4, 5};

    Iterable<Integer> values = Fluent.of(ints);

    assertThat(values).containsExactly(1, 2, 3, 4, 5);
  }

  @Test
  public void create_for_long_array() {
    long[] longs = {1L, 2L, 3L, 4L, 5L};

    Iterable<Long> values = Fluent.of(longs);

    assertThat(values).containsExactly(1L, 2L, 3L, 4L, 5L);
  }

  @Test
  public void create_for_double_array() {
    double[] doubles = {1, 2, 3, 4, 5};

    Iterable<Double> values = Fluent.of(doubles);

    assertThat(values).containsExactly(1D, 2D, 3D, 4D, 5D);
  }

  @Test
  public void transform_values() {
    Iterable<String> values = Fluent.of("a", "b").map(String::toUpperCase);

    assertThat(values).containsExactly("A", "B");
  }

  @Test
  public void filter_values() {
    Iterable<Integer> values = Fluent.of(1, 2, 3, 4, 5).filter(v -> v > 3);

    assertThat(values).containsExactly(4, 5);
  }

  @Test
  public void exclude_values() {
    Iterable<Integer> values = Fluent.of(1, 2, 3, 4, 5).exclude(v -> v > 3);

    assertThat(values).containsExactly(1, 2, 3);
  }

  @Test
  public void count_values() {
    long count = Fluent.of(1, 2, 3, 4, 5).size();

    assertThat(count).isEqualTo(5L);
  }

  @Test
  public void count_with_predicate() {
    long count = Fluent.of(1, 2, 3, 4, 5).count(v -> v > 3);

    assertThat(count).isEqualTo(2L);
  }

  @Test
  public void first_value() {
    Optional<Integer> first = Fluent.of(1, 2, 3, 4, 5).first();

    assertThat(first.get()).isEqualTo(1);
  }

  @Test
  public void no_first_value() {
    Optional<Object> first = Fluent.of().first();

    assertThat(first.isPresent()).isFalse();
  }

  @Test(expected = NullPointerException.class)
  public void first_null_value() {
    Fluent.of((Object) null).first();
  }

  @Test
  public void last_value() {
    Optional<Integer> last = Fluent.of(1, 2, 3, 4, 5).last();

    assertThat(last.get()).isEqualTo(5);
  }

  @Test
  public void no_last_value() {
    Optional<Object> last = Fluent.of().last();

    assertThat(last.isPresent()).isFalse();
  }

  @Test(expected = NullPointerException.class)
  public void last_null_value() {
    Fluent.of((Object) null).last();
  }

  @Test
  public void empty() {
    assertThat(Fluent.of().isEmpty()).isTrue();
    assertThat(Fluent.of("VALUE").isEmpty()).isFalse();
  }

  @Test
  public void contains() {
    Fluent<Integer> values = Fluent.of(1, 2);

    assertThat(values.contains(1)).isTrue();
    assertThat(values.contains(2)).isTrue();
    assertThat(values.contains(3)).isFalse();
    assertThat(values.contains(null)).isFalse();
    assertThat(values.contains("DIFFERENT TYPE")).isFalse();
  }

  @Test
  public void index() {
    Fluent<Integer> values = Fluent.of(1, 2);

    assertThat(values.indexOf(1)).isEqualTo(0);
    assertThat(values.indexOf(2)).isEqualTo(1);
    assertThat(values.indexOf(3)).isEqualTo(-1);
    assertThat(values.indexOf(null)).isEqualTo(-1);
    assertThat(values.indexOf("DIFFERENT TYPE")).isEqualTo(-1);
  }

  @Test
  public void to_array() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    Integer[] array = values.toArray(Integer[]::new);

    assertThat(array).containsExactly(1, 2, 3, 4, 5);
  }

  @Test
  public void to_list() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    List<Integer> list = values.toList();

    assertThat(list).containsExactly(1, 2, 3, 4, 5);
  }

  @Test
  public void to_set() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    Set<Integer> set = values.toSet();

    assertThat(set).containsOnly(1, 2, 3, 4, 5);
  }

  @Test
  public void any_match() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    assertThat(values.anyMatch(v -> v > 5)).isFalse();
    assertThat(values.anyMatch(v -> v < 5)).isTrue();
  }

  @Test
  public void all_match() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    assertThat(values.allMatch(v -> v < 5)).isFalse();
    assertThat(values.allMatch(v -> v < 6)).isTrue();
  }

  @Test
  public void none_match() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    assertThat(values.noneMatch(v -> v < 6)).isFalse();
    assertThat(values.noneMatch(v -> v > 6)).isTrue();
  }

  @Test
  public void collect() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    Long count = values.collect(Collectors.counting());

    assertThat(count).isEqualTo(5L);
  }

  @Test
  public void copy_into() {
    List<Integer> list = Fluent.of(1, 2, 3, 4, 5).copyInto(new ArrayList<>());

    assertThat(list).containsExactly(1, 2, 3, 4, 5);
  }

  @Test
  public void join_with_separator() {
    Fluent<Integer> values = Fluent.of(1, null, 3, 4, 5);

    String string = values.join(", ");

    assertThat(string).isEqualTo("1, null, 3, 4, 5");
  }

  @Test
  public void join() {
    Fluent<Integer> values = Fluent.of(1, 2, 3, 4, 5);

    String string = values.join();

    assertThat(string).isEqualTo("12345");
  }

  @Test
  public void exclude_nulls() {
    Fluent<Integer> values = Fluent.of(1, null, 3).notNulls();

    assertThat(values).containsExactly(1, 3);
  }

  @Test
  public void limit() {
    Iterable<Integer> values = Fluent.of(1, 2, 3, 4, 5).limit(2);

    assertThat(values).containsExactly(1, 2);
  }

  @Test
  public void skip() {
    Iterable<Integer> values = Fluent.of(1, 2, 3, 4, 5).skip(2);

    assertThat(values).containsExactly(3, 4, 5);
  }

  @Test
  public void first_match() {
    Optional<Integer> firstMatch = Fluent.of(1, 2, 3, 4, 5).firstMatch(v -> v == 4);

    assertThat(firstMatch.get()).isEqualTo(4);
  }

  @Test
  public void of_type() {
    Fluent<Integer> integers = Fluent.of("a", 2, "b", 3).filter(Integer.class);

    assertThat(integers).containsExactly(2, 3);
  }

  @Test
  public void cycle() {
    Iterable<Integer> integers = Fluent.of(0, 1).cycle().limit(6);

    assertThat(integers).containsExactly(0, 1, 0, 1, 0, 1);
  }

  @Test
  public void for_each() {
    List<String> list = new ArrayList<>();

    Fluent.of("a", "b", "c").forEach(list::add);

    assertThat(list).containsExactly("a", "b", "c");
  }

  @Test
  public void for_each_with_index() {
    List<String> list = new ArrayList<>();

    Fluent.of("a", "b", "c").forEachWithIndex((index, value) -> list.add(index + "-" + value));

    assertThat(list).containsExactly("0-a", "1-b", "2-c");
  }

  @Test
  public void to_stream() {
    Stream<String> stream = Fluent.of("0", "1", "2").stream();

    assertThat(stream.toArray()).containsExactly("0", "1", "2");
  }

  @Test
  public void to_int_stream() {
    IntStream stream = Fluent.of("0", "1", "2").intStream(Integer::parseInt);

    assertThat(stream.iterator()).containsExactly(0, 1, 2);
  }

  @Test
  public void to_long_stream() {
    LongStream stream = Fluent.of("0", "1", "2").longStream(Long::parseLong);

    assertThat(stream.iterator()).containsExactly(0L, 1L, 2L);
  }

  @Test
  public void to_double_stream() {
    DoubleStream stream = Fluent.of("0", "1", "2").doubleStream(Double::parseDouble);

    assertThat(stream.iterator()).containsExactly(0D, 1D, 2D);
  }

  @Test
  public void min() {
    Optional<Integer> min = Fluent.of(4, 3, 1, 5).min((v1, v2) -> (v1 - v2));

    assertThat(min.get()).isEqualTo(1);
  }

  @Test
  public void max() {
    Optional<Integer> min = Fluent.of(4, 3, 1, 5).max((v1, v2) -> (v1 - v2));

    assertThat(min.get()).isEqualTo(5);
  }

  @Test
  public void reduce() {
    int sum = Fluent.of(1, 2, 3, 4).reduce(0, (a, b) -> a + b);

    assertThat(sum).isEqualTo(10);
  }

  @Test
  public void reduce_without_initial_value() {
    Optional<Integer> sum = Fluent.of(1, 2, 3, 4).reduce((a, b) -> a + b);

    assertThat(sum.get()).isEqualTo(10);
  }

  @Test
  public void reduce_without_values() {
    Optional<Integer> sum = Fluent.<Integer>of().reduce((a, b) -> a + b);

    assertThat(sum.isPresent()).isFalse();
  }

  @Test
  public void sorted_list() {
    List<Integer> sorted = Fluent.of(5, 4, 3, 2, 1).toSortedList(Comparator.naturalOrder());

    assertThat(sorted).containsExactly(1, 2, 3, 4, 5);
  }

  @Test
  public void sorted_set() {
    SortedSet<Integer> sortedSet = Fluent.of(5, 4, 3, 2, 1).toSortedSet(Comparator.naturalOrder());

    assertThat(sortedSet).containsExactly(1, 2, 3, 4, 5);
  }

  @Test
  public void unique_index_by() {
    Map<Integer, String> index = Fluent.of("1", "22", "333").uniqueIndex(String::length);

    assertThat(index).containsEntry(1, "1").containsEntry(2, "22").containsEntry(3, "333");
  }

  @Test(expected = IllegalArgumentException.class)
  public void key_conflict() {
    Fluent.of("a", "b").uniqueIndex(String::length);
  }

  @Test
  public void index_by() {
    Map<Integer, List<String>> index = Fluent.of("a", "b", "cc", "dd").index(String::length);

    assertThat(index).containsEntry(1, Arrays.asList("a", "b")).containsEntry(2, Arrays.asList("cc", "dd"));
  }

  @Test
  public void to_map() {
    Map<String, Integer> map = Fluent.of("a", "bb").toMap(String::length);

    assertThat(map).containsEntry("a", 1).containsEntry("bb", 2);
  }

  @Test
  public void get() {
    Fluent<String> values = Fluent.of("a", "bb");

    assertThat(values.get(0)).isEqualTo("a");
    assertThat(values.get(1)).isEqualTo("bb");
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void get_out_of_bounds() {
    Fluent.of("a", "bb").get(3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void get_negative() {
    Fluent.of("a", "bb").get(-1);
  }

  @Test
  public void concatenate_array() {
    Fluent<String> concatenated = Fluent.of("a", "bb").concat("cc", "dd");

    assertThat(concatenated).containsExactly("a", "bb", "cc", "dd");
  }

  @Test
  public void concatenate_iterable() {
    Fluent<String> concatenated = Fluent.of("a", "b").concat(Fluent.of("c", "d"));

    assertThat(concatenated).containsExactly("a", "b", "c", "d");
  }

  @Test
  public void flat_map() {
    Fluent<String> letters = Fluent.of("a-a-a", "b-b").flatMap(s -> Arrays.asList(s.split("-")));

    assertThat(letters).containsExactly("a", "a", "a", "b", "b");
  }

  @Test
  public void get_only_element() {
    String only = Fluent.of("ONLY").getOnlyElement();

    assertThat(only).isEqualTo("ONLY");
  }

  @Test(expected = NoSuchElementException.class)
  public void get_only_element_of_empty_list() {
    Fluent.of().getOnlyElement();
  }
}
