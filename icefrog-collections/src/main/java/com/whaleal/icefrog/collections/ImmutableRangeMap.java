

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.ListUtil;
import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;

import static com.whaleal.icefrog.core.lang.Preconditions.*;


/**
 * A {@link RangeMap} whose contents will never change, with many other important properties
 * detailed at {@link ImmutableCollection}.
 *
 * @author Louis Wasserman
 * 
 */



public class ImmutableRangeMap<K extends Comparable<?>, V> implements RangeMap<K, V>, Serializable {

  private static final ImmutableRangeMap<Comparable<?>, Object> EMPTY =
      new ImmutableRangeMap<>(ImmutableList.of(), ImmutableList.of());

  /**
   * Returns a {@code Collector} that accumulates the input elements into a new {@code
   * ImmutableRangeMap}. As in {@link Builder}, overlapping ranges are not permitted.
   *
   * 
   */
  public static <T extends Object, K extends Comparable<? super K>, V>
      Collector<T, ?, ImmutableRangeMap<K, V>> toImmutableRangeMap(
          Function<? super T, Range<K>> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableRangeMap(keyFunction, valueFunction);
  }

  /**
   * Returns an empty immutable range map.
   *
   * <p><b>Performance note:</b> the instance returned is a singleton.
   */
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of() {
    return (ImmutableRangeMap<K, V>) EMPTY;
  }

  /** Returns an immutable range map mapping a single range to a single value. */
  public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of(Range<K> range, V value) {
    return new ImmutableRangeMap<>(ImmutableList.of(range), ImmutableList.of(value));
  }

  @SuppressWarnings("unchecked")
  public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> copyOf(
      RangeMap<K, ? extends V> rangeMap) {
    if (rangeMap instanceof ImmutableRangeMap) {
      return (ImmutableRangeMap<K, V>) rangeMap;
    }
    Map<Range<K>, ? extends V> map = rangeMap.asMapOfRanges();
    ImmutableList.Builder<Range<K>> rangesBuilder = new ImmutableList.Builder<>(map.size());
    ImmutableList.Builder<V> valuesBuilder = new ImmutableList.Builder<V>(map.size());
    for (Entry<Range<K>, ? extends V> entry : map.entrySet()) {
      rangesBuilder.add(entry.getKey());
      valuesBuilder.add(entry.getValue());
    }
    return new ImmutableRangeMap<>(rangesBuilder.build(), valuesBuilder.build());
  }

  /** Returns a new builder for an immutable range map. */
  public static <K extends Comparable<?>, V> Builder<K, V> builder() {
    return new Builder<>();
  }

  /**
   * A builder for immutable range MapUtil. Overlapping ranges are prohibited.
   *
   * 
   */

  public static final class Builder<K extends Comparable<?>, V> {
    private final List<Entry<Range<K>, V>> entries;

    public Builder() {
      this.entries = ListUtil.list(false);
    }

    /**
     * Associates the specified range with the specified value.
     *
     * @throws IllegalArgumentException if {@code range} is empty
     */

    public Builder<K, V> put(Range<K> range, V value) {
      checkNotNull(range);
      checkNotNull(value);
      checkArgument(!range.isEmpty(), "Range must not be empty, but was %s", range);
      entries.add( new ImmutableEntry(range, value));
      return this;
    }

    /** Copies all associations from the specified range map into this builder. */

    public Builder<K, V> putAll(RangeMap<K, ? extends V> rangeMap) {
      for (Entry<Range<K>, ? extends V> entry : rangeMap.asMapOfRanges().entrySet()) {
        put(entry.getKey(), entry.getValue());
      }
      return this;
    }


    Builder<K, V> combine(Builder<K, V> builder) {
      entries.addAll(builder.entries);
      return this;
    }

    /**
     * Returns an {@code ImmutableRangeMap} containing the associations previously added to this
     * builder.
     *
     * @throws IllegalArgumentException if any two ranges inserted into this builder overlap
     */
    public ImmutableRangeMap<K, V> build() {
      Collections.sort(entries, Range.<K>rangeLexOrdering().onKeys());
      ImmutableList.Builder<Range<K>> rangesBuilder = new ImmutableList.Builder<>(entries.size());
      ImmutableList.Builder<V> valuesBuilder = new ImmutableList.Builder<V>(entries.size());
      for (int i = 0; i < entries.size(); i++) {
        Range<K> range = entries.get(i).getKey();
        if (i > 0) {
          Range<K> prevRange = entries.get(i - 1).getKey();
          if (range.isConnected(prevRange) && !range.intersection(prevRange).isEmpty()) {
            throw new IllegalArgumentException(
                "Overlapping ranges: range " + prevRange + " overlaps with entry " + range);
          }
        }
        rangesBuilder.add(range);
        valuesBuilder.add(entries.get(i).getValue());
      }
      return new ImmutableRangeMap<>(rangesBuilder.build(), valuesBuilder.build());
    }
  }

  private final transient ImmutableList<Range<K>> ranges;
  private final transient ImmutableList<V> values;

  ImmutableRangeMap(ImmutableList<Range<K>> ranges, ImmutableList<V> values) {
    this.ranges = ranges;
    this.values = values;
  }

  @Override
  @CheckForNull
  public V get(K key) {
    int index =
        SortedLists.binarySearch(
            ranges,
            Range.lowerBoundFn(),
            Cut.belowValue(key),
            SortedLists.KeyPresentBehavior.ANY_PRESENT,
            SortedLists.KeyAbsentBehavior.NEXT_LOWER);
    if (index == -1) {
      return null;
    } else {
      Range<K> range = ranges.get(index);
      return range.contains(key) ? values.get(index) : null;
    }
  }

  @Override
  @CheckForNull
  public Entry<Range<K>, V> getEntry(K key) {
    int index =
        SortedLists.binarySearch(
            ranges,
            Range.lowerBoundFn(),
            Cut.belowValue(key),
            SortedLists.KeyPresentBehavior.ANY_PRESENT,
            SortedLists.KeyAbsentBehavior.NEXT_LOWER);
    if (index == -1) {
      return null;
    } else {
      Range<K> range = ranges.get(index);
      return range.contains(key) ?  new ImmutableEntry(range, values.get(index)) : null;
    }
  }

  @Override
  public Range<K> span() {
    if (ranges.isEmpty()) {
      throw new NoSuchElementException();
    }
    Range<K> firstRange = ranges.get(0);
    Range<K> lastRange = ranges.get(ranges.size() - 1);
    return Range.create(firstRange.lowerBound, lastRange.upperBound);
  }

  /**
   * Guaranteed to throw an exception and leave the {@code RangeMap} unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override

  public final void put(Range<K> range, V value) {
    throw new UnsupportedOperationException();
  }

  /**
   * Guaranteed to throw an exception and leave the {@code RangeMap} unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override

  public final void putCoalescing(Range<K> range, V value) {
    throw new UnsupportedOperationException();
  }

  /**
   * Guaranteed to throw an exception and leave the {@code RangeMap} unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override

  public final void putAll(RangeMap<K, V> rangeMap) {
    throw new UnsupportedOperationException();
  }

  /**
   * Guaranteed to throw an exception and leave the {@code RangeMap} unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override

  public final void clear() {
    throw new UnsupportedOperationException();
  }

  /**
   * Guaranteed to throw an exception and leave the {@code RangeMap} unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override

  public final void remove(Range<K> range) {
    throw new UnsupportedOperationException();
  }

  /**
   * Guaranteed to throw an exception and leave the {@code RangeMap} unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override

  public final void merge(
      Range<K> range,
      @CheckForNull V value,
      BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ImmutableMap<Range<K>, V> asMapOfRanges() {
    if (ranges.isEmpty()) {
      return ImmutableMap.of();
    }
    RegularImmutableSortedSet<Range<K>> rangeSet =
        new RegularImmutableSortedSet<>(ranges, Range.rangeLexOrdering());
    return new ImmutableSortedMap<>(rangeSet, values);
  }

  @Override
  public ImmutableMap<Range<K>, V> asDescendingMapOfRanges() {
    if (ranges.isEmpty()) {
      return ImmutableMap.of();
    }
    RegularImmutableSortedSet<Range<K>> rangeSet =
        new RegularImmutableSortedSet<>(ranges.reverse(), Range.<K>rangeLexOrdering().reverse());
    return new ImmutableSortedMap<>(rangeSet, values.reverse());
  }

  @Override
  public ImmutableRangeMap<K, V> subRangeMap(final Range<K> range) {
    if (checkNotNull(range).isEmpty()) {
      return ImmutableRangeMap.of();
    } else if (ranges.isEmpty() || range.encloses(span())) {
      return this;
    }
    int lowerIndex =
        SortedLists.binarySearch(
            ranges,
            Range.upperBoundFn(),
            range.lowerBound,
            SortedLists.KeyPresentBehavior.FIRST_AFTER,
            SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    int upperIndex =
        SortedLists.binarySearch(
            ranges,
            Range.lowerBoundFn(),
            range.upperBound,
            SortedLists.KeyPresentBehavior.ANY_PRESENT,
            SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    if (lowerIndex >= upperIndex) {
      return ImmutableRangeMap.of();
    }
    final int off = lowerIndex;
    final int len = upperIndex - lowerIndex;
    ImmutableList<Range<K>> subRanges =
        new ImmutableList<Range<K>>() {
          @Override
          public int size() {
            return len;
          }

          @Override
          public Range<K> get(int index) {
            checkElementIndex(index, len);
            if (index == 0 || index == len - 1) {
              return ranges.get(index + off).intersection(range);
            } else {
              return ranges.get(index + off);
            }
          }

          @Override
          boolean isPartialView() {
            return true;
          }
        };
    final ImmutableRangeMap<K, V> outer = this;
    return new ImmutableRangeMap<K, V>(subRanges, values.subList(lowerIndex, upperIndex)) {
      @Override
      public ImmutableRangeMap<K, V> subRangeMap(Range<K> subRange) {
        if (range.isConnected(subRange)) {
          return outer.subRangeMap(subRange.intersection(range));
        } else {
          return ImmutableRangeMap.of();
        }
      }
    };
  }

  @Override
  public int hashCode() {
    return asMapOfRanges().hashCode();
  }

  @Override
  public boolean equals(@CheckForNull Object o) {
    if (o instanceof RangeMap) {
      RangeMap<?, ?> rangeMap = (RangeMap<?, ?>) o;
      return asMapOfRanges().equals(rangeMap.asMapOfRanges());
    }
    return false;
  }

  @Override
  public String toString() {
    return asMapOfRanges().toString();
  }

  /**
   * This class is used to serialize ImmutableRangeMap instances. Serializes the {@link
   * #asMapOfRanges()} form.
   */
  private static class SerializedForm<K extends Comparable<?>, V> implements Serializable {

    private final ImmutableMap<Range<K>, V> mapOfRanges;

    SerializedForm(ImmutableMap<Range<K>, V> mapOfRanges) {
      this.mapOfRanges = mapOfRanges;
    }

    Object readResolve() {
      if (mapOfRanges.isEmpty()) {
        return of();
      } else {
        return createRangeMap();
      }
    }

    Object createRangeMap() {
      Builder<K, V> builder = new Builder<>();
      for (Entry<Range<K>, V> entry : mapOfRanges.entrySet()) {
        builder.put(entry.getKey(), entry.getValue());
      }
      return builder.build();
    }

    private static final long serialVersionUID = 0;
  }

  Object writeReplace() {
    return new SerializedForm<>(asMapOfRanges());
  }

  private static final long serialVersionUID = 0;
}
