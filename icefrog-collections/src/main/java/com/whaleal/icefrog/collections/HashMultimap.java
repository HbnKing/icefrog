

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.lang.Preconditions;
import com.whaleal.icefrog.core.map.MapUtil;





import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;





/**
 * Implementation of {@link Multimap} using hash tables.
 *
 * <p>The multimap does not store duplicate key-value pairs. Adding a new key-value pair equal to an
 * existing key-value pair has no effect.
 *
 * <p>Keys and values may be null. All optional multimap methods are supported, and all returned
 * views are modifiable.
 *
 * <p>This class is not threadsafe when any concurrent operations update the multimap. Concurrent
 * read operations will work correctly if the last write <i>happens-before</i> any reads. To allow
 * concurrent update operations, wrap your multimap with a call to {@link
 * Multimaps#synchronizedSetMultimap}.
 *
 * <p><b>Warning:</b> Do not modify either a key <i>or a value</i> of a {@code HashMultimap} in a
 * way that affects its {@link Object#equals} behavior. Undefined behavior and bugs will result.
 *
 * @author Jared Levy
 * 
 */


public final class HashMultimap<K extends Object, V extends Object>
    extends HashMultimapGwtSerializationDependencies<K, V> {
  private static final int DEFAULT_VALUES_PER_KEY = 2;

  transient int expectedValuesPerKey = DEFAULT_VALUES_PER_KEY;

  /**
   * Creates a new, empty {@code HashMultimap} with the default initial capacities.
   *
   * <p>This method will soon be deprecated in favor of {@code
   * MultimapBuilder.hashKeys().hashSetValues().build()}.
   */
  public static <K extends Object, V extends Object>
      HashMultimap<K, V> create() {
    return new HashMultimap<>();
  }

  /**
   * Constructs an empty {@code HashMultimap} with enough capacity to hold the specified numbers of
   * keys and values without rehashing.
   *
   * <p>This method will soon be deprecated in favor of {@code
   * MultimapBuilder.hashKeys(expectedKeys).hashSetValues(expectedValuesPerKey).build()}.
   *
   * @param expectedKeys the expected number of distinct keys
   * @param expectedValuesPerKey the expected average number of values per key
   * @throws IllegalArgumentException if {@code expectedKeys} or {@code expectedValuesPerKey} is
   *     negative
   */
  public static <K extends Object, V extends Object> HashMultimap<K, V> create(
      int expectedKeys, int expectedValuesPerKey) {
    return new HashMultimap<>(expectedKeys, expectedValuesPerKey);
  }

  /**
   * Constructs a {@code HashMultimap} with the same mappings as the specified multimap. If a
   * key-value mapping appears multiple times in the input multimap, it only appears once in the
   * constructed multimap.
   *
   * <p>This method will soon be deprecated in favor of {@code
   * MultimapBuilder.hashKeys().hashSetValues().build(multimap)}.
   *
   * @param multimap the multimap whose contents are copied to this multimap
   */
  public static <K extends Object, V extends Object> HashMultimap<K, V> create(
      Multimap<? extends K, ? extends V> multimap) {
    return new HashMultimap<>(multimap);
  }

  private HashMultimap() {
    this(12, DEFAULT_VALUES_PER_KEY);
  }

  private HashMultimap(int expectedKeys, int expectedValuesPerKey) {
    super(Platform.newHashMap(expectedKeys));
    Preconditions.checkArgument(expectedValuesPerKey >= 0);
    this.expectedValuesPerKey = expectedValuesPerKey;
  }

  private HashMultimap(Multimap<? extends K, ? extends V> multimap) {
    super(Platform.newHashMap(multimap.keySet().size()));
    putAll(multimap);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Creates an empty {@code HashSet} for a collection of values for one key.
   *
   * @return a new {@code HashSet} containing a collection of values for one key
   */
  @Override
  Set<V> createCollection() {
    return Platform.newHashSetWithExpectedSize(expectedValuesPerKey);
  }

  /**
   * @serialData expectedValuesPerKey, number of distinct keys, and then for each distinct key: the
   *     key, number of values for that key, and the key's values
   */
 // java.io.ObjectOutputStream
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    Serialization.writeMultimap(this, stream);
  }

 // java.io.ObjectInputStream
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    expectedValuesPerKey = DEFAULT_VALUES_PER_KEY;
    int distinctKeys = Serialization.readCount(stream);
    Map<K, Collection<V>> map = Platform.newHashMap(12);
    setMap(map);
    Serialization.populateMultimap(this, stream, distinctKeys);
  }

 // Not needed in emulated source
  private static final long serialVersionUID = 0;
}
