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

import static java.util.Objects.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

@FunctionalInterface
public interface Fluent<T> extends Iterable<T> {
  Stream<T> stream();

  static <T> Fluent<T> of() {
    return Stream::empty;
  }

  @SafeVarargs
  static <T> Fluent<T> of(T... values) {
    requireNonNull(values);
    return () -> Stream.of(values);
  }

  static Fluent<Integer> of(int[] values) {
    requireNonNull(values);
    return () -> IntStream.of(values).boxed();
  }

  static Fluent<Double> of(double[] values) {
    requireNonNull(values);
    return () -> DoubleStream.of(values).boxed();
  }

  static Fluent<Long> of(long[] values) {
    requireNonNull(values);
    return () -> LongStream.of(values).boxed();
  }

  static <T> Fluent<T> of(Iterable<T> values) {
    requireNonNull(values);
    return (values instanceof Fluent<?>) ? (Fluent<T>) values : () -> StreamSupport.stream(values.spliterator(), false);
  }

  static <T> Fluent<T> of(Iterator<T> values) {
    requireNonNull(values);
    return () -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(values, 0), false);
  }

  static <T> Fluent<T> of(Stream<T> stream) {
    requireNonNull(stream);
    return () -> stream;
  }

  static Fluent<String> ofChars(String text) {
    requireNonNull(text);

    return of(Stream.generate(new Supplier<String>() {
      int index = 0;

      @Override
      public String get() {
        return text.substring(index, ++index);
      }
    })).limit(text.length());
  }

  static Fluent<String> split(String text, String regex) {
    requireNonNull(text);
    requireNonNull(regex);
    return of(text.split(regex));
  }

  public default void forEach(Consumer<? super T> action) {
    requireNonNull(action);
    stream().forEach(action);
  }

  public default <R> Fluent<R> map(Function<? super T, ? extends R> transform) {
    requireNonNull(transform);
    return () -> stream().map(transform);
  }

  public default IntStream mapToInt(ToIntFunction<? super T> transform) {
    requireNonNull(transform);
    return stream().mapToInt(transform);
  }

  public default LongStream mapToLong(ToLongFunction<? super T> transform) {
    requireNonNull(transform);
    return stream().mapToLong(transform);
  }

  public default DoubleStream mapToDouble(ToDoubleFunction<? super T> transform) {
    requireNonNull(transform);
    return stream().mapToDouble(transform);
  }

  public default IntStream flatMapToInt(Function<? super T, ? extends IntStream> transform) {
    requireNonNull(transform);
    return stream().flatMapToInt(transform);
  }

  public default LongStream flatMapToLong(Function<? super T, ? extends LongStream> transform) {
    requireNonNull(transform);
    return stream().flatMapToLong(transform);
  }

  public default DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> transform) {
    requireNonNull(transform);
    return stream().flatMapToDouble(transform);
  }

  public default Fluent<T> filter(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return () -> stream().filter(predicate);
  }

  public default Fluent<T> exclude(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return () -> stream().filter(predicate.negate());
  }

  @SuppressWarnings("unchecked")
  public default <R> Fluent<R> filter(Class<R> type) {
    requireNonNull(type);
    return (Fluent<R>) filter(value -> type.isInstance(value));
  }

  public default long size() {
    return stream().count();
  }

  public default long count(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return stream().filter(predicate).count();
  }

  public default Optional<T> first() {
    return stream().findFirst();
  }

  public default Optional<T> any() {
    return stream().findAny();
  }

  public default Fluent<T> skip(long n) {
    return () -> stream().skip(n);
  }

  public default Optional<T> firstMatch(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return stream().filter(predicate).findFirst();
  }

  public default Optional<T> last() {
    return stream().reduce((l, r) -> r);
  }

  public default boolean isEmpty() {
    return !iterator().hasNext();
  }

  public default String join(CharSequence delimiter) {
    requireNonNull(delimiter);
    StringJoiner joiner = new StringJoiner(delimiter);
    forEach(value -> joiner.add(String.valueOf(value)));
    return joiner.toString();
  }

  public default String join() {
    return join("");
  }

  public default boolean contains(Object element) {
    return stream().anyMatch(Predicate.isEqual(element));
  }

  public default int indexOf(Object element) {
    int index = 0;
    for (T value : this) {
      if (Objects.equals(value, element)) {
        return index;
      }
      index++;
    }
    return -1;
  }

  public default T[] toArray(IntFunction<T[]> generator) {
    requireNonNull(generator);
    return stream().toArray(generator);
  }

  public default List<T> toList() {
    return copyInto(new ArrayList<>());
  }

  public default List<T> toSortedList(Comparator<? super T> comparator) {
    requireNonNull(comparator);
    List<T> list = copyInto(new ArrayList<>());
    Collections.sort(list, comparator);
    return list;
  }

  public default SortedSet<T> toSortedSet(Comparator<? super T> comparator) {
    requireNonNull(comparator);
    return copyInto(new TreeSet<>(comparator));
  }

  public default Set<T> toSet() {
    return copyInto(new HashSet<>());
  }

  public default boolean anyMatch(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return stream().anyMatch(predicate);
  }

  public default boolean allMatch(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return stream().allMatch(predicate);
  }

  public default boolean noneMatch(Predicate<? super T> predicate) {
    requireNonNull(predicate);
    return stream().noneMatch(predicate);
  }

  public default <R, A> R collect(Collector<? super T, A, ? extends R> collector) {
    requireNonNull(collector);
    return stream().collect(collector);
  }

  public default Fluent<T> limit(int limitSize) {
    if (limitSize < 0) {
      throw new IllegalArgumentException("limit is negative");
    }
    return () -> stream().limit(limitSize);
  }

  public default Fluent<T> cycle() {
    return () -> Stream.generate(() -> stream()).flatMap(s -> s);
  }

  public default void forEachWithIndex(BiConsumer<Integer, T> consumer) {
    requireNonNull(consumer);
    if (isParallel()) {
      AtomicInteger index = new AtomicInteger(0);
      forEach(value -> consumer.accept(index.getAndIncrement(), value));
    } else {
      int index = 0;
      for (T value : this) {
        consumer.accept(index++, value);
      }
    }
  }

  public default Fluent<T> peek(Consumer<? super T> action) {
    requireNonNull(action);
    return () -> stream().peek(action);
  }

  public default void forEachOrdered(Consumer<? super T> action) {
    requireNonNull(action);
    stream().forEachOrdered(action);
  }

  public default Iterator<T> iterator() {
    return stream().iterator();
  }

  public default Spliterator<T> spliterator() {
    return stream().spliterator();
  }

  public default boolean isParallel() {
    return stream().isParallel();
  }

  public default Fluent<T> sequential() {
    return () -> stream().sequential();
  }

  public default Fluent<T> unordered() {
    return () -> stream().unordered();
  }

  public default IntStream intStream(ToIntFunction<? super T> mapper) {
    requireNonNull(mapper);
    return stream().mapToInt(mapper);
  }

  public default LongStream longStream(ToLongFunction<? super T> mapper) {
    requireNonNull(mapper);
    return stream().mapToLong(mapper);
  }

  public default DoubleStream doubleStream(ToDoubleFunction<? super T> mapper) {
    requireNonNull(mapper);
    return stream().mapToDouble(mapper);
  }

  public default Optional<T> min(Comparator<? super T> comparator) {
    requireNonNull(comparator);
    return stream().min(comparator);
  }

  public default Optional<T> max(Comparator<? super T> comparator) {
    requireNonNull(comparator);
    return stream().max(comparator);
  }

  public default <C extends Collection<T>> C copyInto(C collection) {
    requireNonNull(collection);
    return stream().collect(Collectors.toCollection(() -> collection));
  }

  public default T reduce(T identity, BinaryOperator<T> accumulator) {
    requireNonNull(accumulator);
    return stream().reduce(identity, accumulator);
  }

  public default Optional<T> reduce(BinaryOperator<T> accumulator) {
    requireNonNull(accumulator);
    return stream().reduce(accumulator);
  }

  public default <K> Map<K, T> uniqueIndex(Function<? super T, K> toKey) {
    requireNonNull(toKey);
    Map<K, T> map = new HashMap<>();

    forEach(value -> {
      K key = toKey.apply(value);
      if (null != map.put(key, value)) {
        throw new IllegalArgumentException("Same key used twice" + key);
      }
    });

    return map;
  }

  public default <K> Map<K, List<T>> index(Function<? super T, K> toKey) {
    requireNonNull(toKey);
    Map<K, List<T>> multiMap = new HashMap<>();

    forEach(value -> {
      K key = toKey.apply(value);

      List<T> list = multiMap.computeIfAbsent(key, k -> new ArrayList<T>());
      list.add(value);
    });

    return multiMap;
  }

  public default <V> Map<T, V> toMap(Function<? super T, V> toValue) {
    requireNonNull(toValue);
    Map<T, V> map = new HashMap<>();

    forEach(key -> {
      V value = toValue.apply(key);
      if (null != map.put(key, value)) {
        throw new IllegalArgumentException("Same key used twice" + key);
      }
    });

    return map;
  }

  public default <K, V> Map<K, V> toMap(Function<? super T, K> toKey, Function<? super T, V> toValue) {
    requireNonNull(toKey);
    requireNonNull(toValue);
    Map<K, V> map = new HashMap<>();

    forEach(item -> {
      K key = toKey.apply(item);
      V value = toValue.apply(item);
      if (null != map.put(key, value)) {
        throw new IllegalArgumentException("Same key used twice" + key);
      }
    });

    return map;
  }

  public default T get(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("index is negative");
    }

    int current = 0;
    for (T next : this) {
      if (current == index) {
        return next;
      }
      current++;
    }
    throw new ArrayIndexOutOfBoundsException();
  }

  public default <V, L extends List<V>> Fluent<V> flatMap(Function<? super T, L> toList) {
    requireNonNull(toList);
    return () -> {
      Function<? super L, Stream<V>> toStream = l -> l.stream();
      Function<? super T, Stream<V>> toListAndThenToStream = toList.andThen(toStream);
      return stream().flatMap(toListAndThenToStream);
    };
  }

  public default Fluent<T> parallel() {
    return () -> stream().parallel();
  }

  public default Fluent<T> sorted() {
    return () -> stream().sorted();
  }

  public default Fluent<T> distinct() {
    return () -> stream().distinct();
  }

  public default Fluent<T> sorted(Comparator<? super T> comparator) {
    requireNonNull(comparator);
    return () -> stream().sorted(comparator);
  }

  public default Fluent<T> reversed(Comparator<? super T> comparator) {
    requireNonNull(comparator);
    return () -> stream().sorted(comparator.reversed());
  }

  public default <C extends Comparable<? super C>> Fluent<T> sortedOn(Function<? super T, C> comparator) {
    requireNonNull(comparator);
    return () -> stream().sorted((l, r) -> comparator.apply(l).compareTo(comparator.apply(r)));
  }

  public default <C extends Comparable<? super C>> Fluent<T> reversedOn(Function<? super T, C> comparator) {
    requireNonNull(comparator);
    return () -> stream().sorted((l, r) -> comparator.apply(r).compareTo(comparator.apply(l)));
  }

  //@SafeVarargs
  public default Fluent<T> concat(T... values) {
    requireNonNull(values);
    return () -> Stream.concat(stream(), Stream.of(values));
  }

  public default Fluent<T> concat(Iterable<T> values) {
    requireNonNull(values);
    return () -> Stream.concat(stream(), StreamSupport.stream(values.spliterator(), false));
  }

  public default T getOnlyElement() {
    return first().get();
  }

  public default Fluent<T> notNulls() {
    return filter(v -> v != null);
  }

  public default <K> Map<K, List<T>> groupBy(Function<? super T, ? extends K> classifier) {
    requireNonNull(classifier);
    return stream().collect(Collectors.groupingBy(classifier, HashMap::new, Collectors.toList()));
  }

  public default <K, M extends Map<K, List<T>>> Map<K, List<T>> groupBy(Function<? super T, ? extends K> classifier, Supplier<M> mapFactory) {
    requireNonNull(classifier);
    return stream().collect(Collectors.groupingBy(classifier, mapFactory, Collectors.toList()));
  }
}
