

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.lang.Preconditions;
import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNonnegative;
import static java.util.Objects.requireNonNull;


/**
 * A {@link BiMap} whose contents will never change, with many other important properties detailed
 * at {@link ImmutableCollection}.
 *
 * @author Jared Levy
 * 
 */


public abstract class ImmutableBiMap<K, V> extends ImmutableBiMapFauxverideShim<K, V>
    implements BiMap<K, V> {

  /**
   * Returns a {@link Collector} that accumulates elements into an {@code ImmutableBiMap} whose keys
   * and values are the result of applying the provided mapping functions to the input elements.
   * Entries appear in the result {@code ImmutableBiMap} in encounter order.
   *
   * <p>If the mapped keys or values contain duplicates (according to {@link Object#equals(Object)},
   * an {@code IllegalArgumentException} is thrown when the collection operation is performed. (This
   * differs from the {@code Collector} returned by {@link Collectors#toMap(Function, Function)},
   * which throws an {@code IllegalStateException}.)
   *
   * 
   */
  public static <T extends Object, K, V>
      Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableBiMap(keyFunction, valueFunction);
  }

  /**
   * Returns the empty bimap.
   *
   * <p><b>Performance note:</b> the instance returned is a singleton.
   */
  // Casting to any type is safe because the set will never hold any elements.
  @SuppressWarnings("unchecked")
  public static <K, V> ImmutableBiMap<K, V> of() {
    return (ImmutableBiMap<K, V>) RegularImmutableBiMap.EMPTY;
  }

  /** Returns an immutable bimap containing a single entry. */
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1) {
    return new SingletonImmutableBiMap<>(k1, v1);
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   */
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2) {
    return RegularImmutableBiMap.fromEntries(entryOf(k1, v1), entryOf(k2, v2));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   */
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return RegularImmutableBiMap.fromEntries(entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   */
  public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return RegularImmutableBiMap.fromEntries(
        entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   */
  public static <K, V> ImmutableBiMap<K, V> of(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return RegularImmutableBiMap.fromEntries(
        entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   * 
   */
  public static <K, V> ImmutableBiMap<K, V> of(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    return RegularImmutableBiMap.fromEntries(
        entryOf(k1, v1),
        entryOf(k2, v2),
        entryOf(k3, v3),
        entryOf(k4, v4),
        entryOf(k5, v5),
        entryOf(k6, v6));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   * 
   */
  public static <K, V> ImmutableBiMap<K, V> of(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
    return RegularImmutableBiMap.fromEntries(
        entryOf(k1, v1),
        entryOf(k2, v2),
        entryOf(k3, v3),
        entryOf(k4, v4),
        entryOf(k5, v5),
        entryOf(k6, v6),
        entryOf(k7, v7));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   * 
   */
  public static <K, V> ImmutableBiMap<K, V> of(
      K k1,
      V v1,
      K k2,
      V v2,
      K k3,
      V v3,
      K k4,
      V v4,
      K k5,
      V v5,
      K k6,
      V v6,
      K k7,
      V v7,
      K k8,
      V v8) {
    return RegularImmutableBiMap.fromEntries(
        entryOf(k1, v1),
        entryOf(k2, v2),
        entryOf(k3, v3),
        entryOf(k4, v4),
        entryOf(k5, v5),
        entryOf(k6, v6),
        entryOf(k7, v7),
        entryOf(k8, v8));
  }

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   * 
   */
  public static <K, V> ImmutableBiMap<K, V> of(
      K k1,
      V v1,
      K k2,
      V v2,
      K k3,
      V v3,
      K k4,
      V v4,
      K k5,
      V v5,
      K k6,
      V v6,
      K k7,
      V v7,
      K k8,
      V v8,
      K k9,
      V v9) {
    return RegularImmutableBiMap.fromEntries(
        entryOf(k1, v1),
        entryOf(k2, v2),
        entryOf(k3, v3),
        entryOf(k4, v4),
        entryOf(k5, v5),
        entryOf(k6, v6),
        entryOf(k7, v7),
        entryOf(k8, v8),
        entryOf(k9, v9));
  }
  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are added
   * 
   */
  public static <K, V> ImmutableBiMap<K, V> of(
      K k1,
      V v1,
      K k2,
      V v2,
      K k3,
      V v3,
      K k4,
      V v4,
      K k5,
      V v5,
      K k6,
      V v6,
      K k7,
      V v7,
      K k8,
      V v8,
      K k9,
      V v9,
      K k10,
      V v10) {
    return RegularImmutableBiMap.fromEntries(
        entryOf(k1, v1),
        entryOf(k2, v2),
        entryOf(k3, v3),
        entryOf(k4, v4),
        entryOf(k5, v5),
        entryOf(k6, v6),
        entryOf(k7, v7),
        entryOf(k8, v8),
        entryOf(k9, v9),
        entryOf(k10, v10));
  }

  // looking for of() with > 10 entries? Use the builder or ofEntries instead.

  /**
   * Returns an immutable map containing the given entries, in order.
   *
   * @throws IllegalArgumentException if duplicate keys or values are provided
   * 
   */
  @SafeVarargs
  public static <K, V> ImmutableBiMap<K, V> ofEntries(Entry<? extends K, ? extends V>... entries) {
    @SuppressWarnings("unchecked") // we will only ever read these
    Entry<K, V>[] entries2 = (Entry<K, V>[]) entries;
    return RegularImmutableBiMap.fromEntries(entries2);
  }

  /**
   * Returns a new builder. The generated builder is equivalent to the builder created by the {@link
   * Builder} constructor.
   */
  public static <K, V> Builder<K, V> builder() {
    return new Builder<>();
  }

  /**
   * Returns a new builder, expecting the specified number of entries to be added.
   *
   * <p>If {@code expectedSize} is exactly the number of entries added to the builder before {@link
   * Builder#build} is called, the builder is likely to perform better than an unsized {@link
   * #builder()} would have.
   *
   * <p>It is not specified if any performance benefits apply if {@code expectedSize} is close to,
   * but not exactly, the number of entries added to the builder.
   *
   * 
   */

  public static <K, V> Builder<K, V> builderWithExpectedSize(int expectedSize) {
    checkNonnegative(expectedSize, "expectedSize");
    return new Builder<>(expectedSize);
  }

  /**
   * A builder for creating immutable bimap instances, especially {@code public static final} biMapUtil
   * ("constant biMapUtil"). Example:
   *
   * <pre>{@code
   * static final ImmutableBiMap<String, Integer> WORD_TO_INT =
   *     new ImmutableBiMap.Builder<String, Integer>()
   *         .put("one", 1)
   *         .put("two", 2)
   *         .put("three", 3)
   *         .buildOrThrow();
   * }</pre>
   *
   * <p>For <i>small</i> immutable biMapUtil, the {@code ImmutableBiMap.of()} methods are even more
   * convenient.
   *
   * <p>By default, a {@code Builder} will generate biMapUtil that iterate over entries in the order
   * they were inserted into the builder. For example, in the above example, {@code
   * WORD_TO_INT.entrySet()} is guaranteed to iterate over the entries in the order {@code "one"=1,
   * "two"=2, "three"=3}, and {@code keySet()} and {@code values()} respect the same order. If you
   * want a different order, consider using {@link #orderEntriesByValue(Comparator)}, which changes
   * this builder to sort entries by value.
   *
   * <p>Builder instances can be reused - it is safe to call {@link #buildOrThrow} multiple times to
   * build multiple biMapUtil in series. Each bimap is a superset of the biMapUtil created before it.
   *
   * 
   */
  public static final class Builder<K, V> extends ImmutableMap.Builder<K, V> {

    /**
     * Creates a new builder. The returned builder is equivalent to the builder generated by {@link
     * ImmutableBiMap#builder}.
     */
    public Builder() {}

    Builder(int size) {
      super(size);
    }

    /**
     * Associates {@code key} with {@code value} in the built bimap. Duplicate keys or values are
     * not allowed, and will cause {@link #build} to fail.
     */
    
    @Override
    public Builder<K, V> put(K key, V value) {
      super.put(key, value);
      return this;
    }

    /**
     * Adds the given {@code entry} to the bimap. Duplicate keys or values are not allowed, and will
     * cause {@link #build} to fail.
     *
     * 
     */
    
    @Override
    public Builder<K, V> put(Entry<? extends K, ? extends V> entry) {
      super.put(entry);
      return this;
    }

    /**
     * Associates all of the given map's keys and values in the built bimap. Duplicate keys or
     * values are not allowed, and will cause {@link #build} to fail.
     *
     * @throws NullPointerException if any key or value in {@code map} is null
     */
    
    @Override
    public Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
      super.putAll(map);
      return this;
    }

    /**
     * Adds all of the given entries to the built bimap. Duplicate keys or values are not allowed,
     * and will cause {@link #build} to fail.
     *
     * @throws NullPointerException if any key, value, or entry is null
     * 
     */
    

    @Override
    public Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> entries) {
      super.putAll(entries);
      return this;
    }

    /**
     * Configures this {@code Builder} to order entries by value according to the specified
     * comparator.
     *
     * <p>The sort order is stable, that is, if two entries have values that compare as equivalent,
     * the entry that was inserted first will be first in the built map's iteration order.
     *
     * @throws IllegalStateException if this method was already called
     * 
     */
    

    @Override
    public Builder<K, V> orderEntriesByValue(Comparator<? super V> valueComparator) {
      super.orderEntriesByValue(valueComparator);
      return this;
    }

    @Override
    
    Builder<K, V> combine(ImmutableMap.Builder<K, V> builder) {
      super.combine(builder);
      return this;
    }

    /**
     * Returns a newly-created immutable bimap. The iteration order of the returned bimap is the
     * order in which entries were inserted into the builder, unless {@link #orderEntriesByValue}
     * was called, in which case entries are sorted by value.
     *
     * <p>Prefer the equivalent method {@link #buildOrThrow()} to make it explicit that the method
     * will throw an exception if there are duplicate keys or values. The {@code build()} method
     * will soon be deprecated.
     *
     * @throws IllegalArgumentException if duplicate keys or values were added
     */
    @Override
    public ImmutableBiMap<K, V> build() {
      return buildOrThrow();
    }

    /**
     * Returns a newly-created immutable bimap, or throws an exception if any key or value was added
     * more than once. The iteration order of the returned bimap is the order in which entries were
     * inserted into the builder, unless {@link #orderEntriesByValue} was called, in which case
     * entries are sorted by value.
     *
     * @throws IllegalArgumentException if duplicate keys or values were added
     * 
     */
    @Override
    public ImmutableBiMap<K, V> buildOrThrow() {
      switch (size) {
        case 0:
          return of();
        case 1:
          // requireNonNull is safe because the first `size` elements have been filled in.
          Entry<K, V> onlyEntry = requireNonNull(entries[0]);
          return of(onlyEntry.getKey(), onlyEntry.getValue());
        default:
          /*
           * If entries is full, or if hash flooding is detected, then this implementation may end
           * up using the entries array directly and writing over the entry objects with
           * non-terminal entries, but this is safe; if this Builder is used further, it will grow
           * the entries array (so it can't affect the original array), and future build() calls
           * will always copy any entry objects that cannot be safely reused.
           */
          if (valueComparator != null) {
            if (entriesUsed) {
              entries = Arrays.copyOf(entries, size);
            }
            Arrays.sort(
                entries,
                0,
                size,
                Ordering.from(valueComparator).onResultOf(MapUtil.valueFunction()));
          }
          entriesUsed = true;
          return RegularImmutableBiMap.fromEntryArray(size, entries);
      }
    }

    @Override

    ImmutableBiMap<K, V> buildJdkBacked() {
      Preconditions.checkState(
          valueComparator == null,
          "buildJdkBacked is for tests only, doesn't support orderEntriesByValue");
      switch (size) {
        case 0:
          return of();
        case 1:
          // requireNonNull is safe because the first `size` elements have been filled in.
          Entry<K, V> onlyEntry = requireNonNull(entries[0]);
          return of(onlyEntry.getKey(), onlyEntry.getValue());
        default:
          entriesUsed = true;
          return RegularImmutableBiMap.fromEntryArray(size, entries);
      }
    }
  }

  /**
   * Returns an immutable bimap containing the same entries as {@code map}. If {@code map} somehow
   * contains entries with duplicate keys (for example, if it is a {@code SortedMap} whose
   * comparator is not <i>consistent with equals</i>), the results of this method are undefined.
   *
   * <p>The returned {@code BiMap} iterates over entries in the same order as the {@code entrySet}
   * of the original map.
   *
   * <p>Despite the method name, this method attempts to avoid actually copying the data when it is
   * safe to do so. The exact circumstances under which a copy will or will not be performed are
   * undocumented and subject to change.
   *
   * @throws IllegalArgumentException if two keys have the same value or two values have the same
   *     key
   * @throws NullPointerException if any key or value in {@code map} is null
   */
  public static <K, V> ImmutableBiMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
    if (map instanceof ImmutableBiMap) {
      @SuppressWarnings("unchecked") // safe since map is not writable
      ImmutableBiMap<K, V> bimap = (ImmutableBiMap<K, V>) map;
      // TODO(lowasser): if we need to make a copy of a BiMap because the
      // forward map is a view, don't make a copy of the non-view delegate map
      if (!bimap.isPartialView()) {
        return bimap;
      }
    }
    return copyOf(map.entrySet());
  }

  /**
   * Returns an immutable bimap containing the given entries. The returned bimap iterates over
   * entries in the same order as the original iterable.
   *
   * @throws IllegalArgumentException if two keys have the same value or two values have the same
   *     key
   * @throws NullPointerException if any key, value, or entry is null
   * 
   */

  public static <K, V> ImmutableBiMap<K, V> copyOf(
      Iterable<? extends Entry<? extends K, ? extends V>> entries) {
    @SuppressWarnings("unchecked") // we'll only be using getKey and getValue, which are covariant
    Entry<K, V>[] entryArray = (Entry<K, V>[]) Iterables.toArray(entries, EMPTY_ENTRY_ARRAY);
    switch (entryArray.length) {
      case 0:
        return of();
      case 1:
        Entry<K, V> entry = entryArray[0];
        return of(entry.getKey(), entry.getValue());
      default:
        /*
         * The current implementation will end up using entryArray directly, though it will write
         * over the (arbitrary, potentially mutable) Entry objects actually stored in entryArray.
         */
        return RegularImmutableBiMap.fromEntries(entryArray);
    }
  }

  ImmutableBiMap() {}

  /**
   * {@inheritDoc}
   *
   * <p>The inverse of an {@code ImmutableBiMap} is another {@code ImmutableBiMap}.
   */
  @Override
  public abstract ImmutableBiMap<V, K> inverse();

  /**
   * Returns an immutable set of the values in this map, in the same order they appear in .
   */
  @Override
  public ImmutableSet<V> values() {
    return inverse().keySet();
  }

  @Override
  final ImmutableSet<V> createValues() {
    throw new AssertionError("should never be called");
  }

  /**
   * Guaranteed to throw an exception and leave the bimap unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  
  @Deprecated
  @Override

  @CheckForNull
  public final V forcePut(K key, V value) {
    throw new UnsupportedOperationException();
  }

  /**
   * Serialized type for all ImmutableBiMap instances. It captures the logical contents and they are
   * reconstructed using public factory methods. This ensures that the implementation types remain
   * as implementation details.
   *
   * <p>Since the bimap is immutable, ImmutableBiMap doesn't require special logic for keeping the
   * bimap and its inverse in sync during serialization, the way AbstractBiMap does.
   */
  private static class SerializedForm<K, V> extends ImmutableMap.SerializedForm<K, V> {
    SerializedForm(ImmutableBiMap<K, V> bimap) {
      super(bimap);
    }

    @Override
    Builder<K, V> makeBuilder(int size) {
      return new Builder<>(size);
    }

    private static final long serialVersionUID = 0;
  }

  @Override
  Object writeReplace() {
    return new SerializedForm<>(this);
  }
}
