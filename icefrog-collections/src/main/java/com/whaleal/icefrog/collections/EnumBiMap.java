

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map;

import static com.whaleal.icefrog.core.lang.Preconditions.checkArgument;
import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;

/**
 * A {@code BiMap} backed by two {@code EnumMap} instances. Null keys and values are not permitted.
 * An {@code EnumBiMap} and its inverse are both serializable.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#bimap"> {@code BiMap}</a>.
 *
 * @author Mike Bostock
 *
 */


public final class EnumBiMap<K extends Enum<K>, V extends Enum<V>> extends AbstractBiMap<K, V> {
  private transient Class<K> keyType;
  private transient Class<V> valueType;

  /**
   * Returns a new, empty {@code EnumBiMap} using the specified key and value types.
   *
   * @param keyType the key type
   * @param valueType the value type
   */
  public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(
      Class<K> keyType, Class<V> valueType) {
    return new EnumBiMap<>(keyType, valueType);
  }

  /**
   * Returns a new bimap with the same mappings as the specified map. If the specified map is an
   * {@code EnumBiMap}, the new bimap has the same types as the provided map. Otherwise, the
   * specified map must contain at least one mapping, in order to determine the key and value types.
   *
   * @param map the map whose mappings are to be placed in this map
   * @throws IllegalArgumentException if map is not an {@code EnumBiMap} instance and contains no
   *     mappings
   */
  public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(Map<K, V> map) {
    EnumBiMap<K, V> bimap = create(inferKeyType(map), inferValueType(map));
    bimap.putAll(map);
    return bimap;
  }

  private EnumBiMap(Class<K> keyType, Class<V> valueType) {
    super(new EnumMap<K, V>(keyType), new EnumMap<V, K>(valueType));
    this.keyType = keyType;
    this.valueType = valueType;
  }

  static <K extends Enum<K>> Class<K> inferKeyType(Map<K, ?> map) {
    if (map instanceof EnumBiMap) {
      return ((EnumBiMap<K, ?>) map).keyType();
    }
    if (map instanceof EnumHashBiMap) {
      return ((EnumHashBiMap<K, ?>) map).keyType();
    }
    checkArgument(!map.isEmpty());
    return map.keySet().iterator().next().getDeclaringClass();
  }

  private static <V extends Enum<V>> Class<V> inferValueType(Map<?, V> map) {
    if (map instanceof EnumBiMap) {
      return ((EnumBiMap<?, V>) map).valueType;
    }
    checkArgument(!map.isEmpty());
    return map.values().iterator().next().getDeclaringClass();
  }

  /** Returns the associated key type. */
  public Class<K> keyType() {
    return keyType;
  }

  /** Returns the associated value type. */
  public Class<V> valueType() {
    return valueType;
  }

  @Override
  K checkKey(K key) {
    return checkNotNull(key);
  }

  @Override
  V checkValue(V value) {
    return checkNotNull(value);
  }

  /**
   * @serialData the key class, value class, number of entries, first key, first value, second key,
   *     second value, and so on.
   */
 // java.io.ObjectOutputStream
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject(keyType);
    stream.writeObject(valueType);
    Serialization.writeMap(this, stream);
  }

  @SuppressWarnings("unchecked") // reading fields populated by writeObject
 // java.io.ObjectInputStream
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    keyType = (Class<K>) stream.readObject();
    valueType = (Class<V>) stream.readObject();
    setDelegates(new EnumMap<K, V>(keyType), new EnumMap<V, K>(valueType));
    Serialization.populateMap(this, stream);
  }

 // not needed in emulated source.
  private static final long serialVersionUID = 0;
}
