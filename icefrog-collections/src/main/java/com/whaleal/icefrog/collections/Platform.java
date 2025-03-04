

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import com.whaleal.icefrog.core.util.StrUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Methods factored out so that they can be emulated differently in GWT.
 *
 * @author Hayward Chan
 */


final class Platform {
  private static final java.util.logging.Logger logger =
      java.util.logging.Logger.getLogger(Platform.class.getName());

  /** Returns the platform preferred implementation of a map based on a hash table. */
  static <K extends Object, V extends Object>
      Map<K, V> newHashMap(int expectedSize) {
    return MapUtil.newHashMap(expectedSize);
  }

  /**
   * Returns the platform preferred implementation of an insertion ordered map based on a hash
   * table.
   */
  static <K extends Object, V extends Object>
      Map<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
    return MapUtil.newHashMap(expectedSize,true);
  }

  /** Returns the platform preferred implementation of a set based on a hash table. */
  static <E extends Object> Set<E> newHashSetWithExpectedSize(int expectedSize) {
    return Sets.newHashSetWithExpectedSize(expectedSize);
  }

  /** Returns the platform preferred implementation of a thread-safe hash set. */
  static <E> Set<E> newConcurrentHashSet() {
    return ConcurrentHashMap.newKeySet();
  }

  /**
   * Returns the platform preferred implementation of an insertion ordered set based on a hash
   * table.
   */
  static <E extends Object> Set<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
    return Sets.newLinkedHashSetWithExpectedSize(expectedSize);
  }

  /**
   * Returns the platform preferred map implementation that preserves insertion order when used only
   * for insertions.
   */
  static <K extends Object, V extends Object>
      Map<K, V> preservesInsertionOrderOnPutsMap() {
    return MapUtil.newHashMap(true);
  }

  /**
   * Returns the platform preferred set implementation that preserves insertion order when used only
   * for insertions.
   */
  static <E extends Object> Set<E> preservesInsertionOrderOnAddsSet() {
    return Sets.newLinkedHashSet();
  }

  /**
   * Returns a new array of the given length with the same type as a reference array.
   *
   * @param reference any array of the desired type
   * @param length the length of the new array
   */
  /*
   * The new array contains nulls, even if the old array did not. If we wanted to be accurate, we
   * would declare a return type of `T[]`. However, we've decided not to think too hard
   * about arrays for now, as they're a mess. (We previously discussed this in the review of
   * ObjectArrays, which is the main caller of this method.)
   */
  static <T extends Object> T[] newArray(T[] reference, int length) {
    Class<?> type = reference.getClass().getComponentType();

    // the cast is safe because
    // result.getClass() == reference.getClass().getComponentType()
    @SuppressWarnings("unchecked")
    T[] result = (T[]) Array.newInstance(type, length);
    return result;
  }

  /** Equivalent to Arrays.copyOfRange(source, from, to, arrayOfType.getClass()). */
  /*
   * Arrays are a mess from a nullness perspective, and Class instances for object-array types are
   * even worse. For now, we just suppress and move on with our lives.
   *
   * - https://github.com/jspecify/jspecify/issues/65
   *
   * - https://github.com/jspecify/jdk/commit/71d826792b8c7ef95d492c50a274deab938f2552
   */
  @SuppressWarnings("nullness")
  static <T extends Object> T[] copy(Object[] source, int from, int to, T[] arrayOfType) {
    return Arrays.copyOfRange(source, from, to, (Class<? extends T[]>) arrayOfType.getClass());
  }

  /**
   * Configures the given map maker to use weak keys, if possible; does nothing otherwise (i.e., in
   * GWT). This is sometimes acceptable, when only server-side code could generate enough volume
   * that reclamation becomes important.
   */
  static MapMaker tryWeakKeys(MapMaker mapMaker) {
    return mapMaker.weakKeys();
  }

  static int reduceIterationsIfGwt(int iterations) {
    return iterations;
  }

  static int reduceExponentIfGwt(int exponent) {
    return exponent;
  }

  static void checkGwtRpcEnabled() {
    String propertyName = "guava.gwt.emergency_reenable_rpc";

    if (!Boolean.parseBoolean(System.getProperty(propertyName, "false"))) {
      throw new UnsupportedOperationException(
          StrUtil.lenientFormat(
              "We are removing GWT-RPC support for Guava types. You can temporarily reenable"
                  + " support by setting the system property %s to true. For more about system"
                  + " properties, see %s. For more about Guava's GWT-RPC support, see %s.",
              propertyName,
              "https://stackoverflow.com/q/5189914/28465",
              "https://groups.google.com/d/msg/guava-announce/zHZTFg7YF3o/rQNnwdHeEwAJ"));
    }
    logger.log(
        java.util.logging.Level.WARNING,
        "Later in 2020, we will remove GWT-RPC support for Guava types. You are seeing this"
            + " warning because you are sending a Guava type over GWT-RPC, which will break. You"
            + " can identify which type by looking at the class name in the attached stack trace.",
        new Throwable());
  }

  private Platform() {}
}
