

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;




import static com.whaleal.icefrog.core.lang.Preconditions.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;


/**
 * Static utility methods pertaining to object arrays.
 *
 * @author Kevin Bourrillion
 * 
 */


public final class ObjectArrays {

  private ObjectArrays() {}

  /**
   * Returns a new array of the given length with the specified component type.
   *
   * @param type the component type
   * @param length the length of the new array
   */
 // Array.newInstance(Class, int)
  @SuppressWarnings("unchecked")
  public static <T> T[] newArray(Class<T> type, int length) {
    return (T[]) Array.newInstance(type, length);
  }

  /**
   * Returns a new array of the given length with the same type as a reference array.
   *
   * @param reference any array of the desired type
   * @param length the length of the new array
   */
  public static <T extends Object> T[] newArray(T[] reference, int length) {
    return Platform.newArray(reference, length);
  }

  /**
   * Returns a new array that contains the concatenated contents of two arrays.
   *
   * @param first the first array of elements to concatenate
   * @param second the second array of elements to concatenate
   * @param type the component type of the returned array
   */
 // Array.newInstance(Class, int)
  public static <T> T[] concat(T[] first, T[] second, Class<T> type) {
    T[] result = newArray(type, first.length + second.length);
    System.arraycopy(first, 0, result, 0, first.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * Returns a new array that prepends {@code element} to {@code array}.
   *
   * @param element the element to prepend to the front of {@code array}
   * @param array the array of elements to append
   * @return an array whose size is one larger than {@code array}, with {@code element} occupying
   *     the first position, and the elements of {@code array} occupying the remaining elements.
   */
  public static <T extends Object> T[] concat(@ParametricNullness T element, T[] array) {
    T[] result = newArray(array, array.length + 1);
    result[0] = element;
    System.arraycopy(array, 0, result, 1, array.length);
    return result;
  }

  /**
   * Returns a new array that appends {@code element} to {@code array}.
   *
   * @param array the array of elements to prepend
   * @param element the element to append to the end
   * @return an array whose size is one larger than {@code array}, with the same contents as {@code
   *     array}, plus {@code element} occupying the last position.
   */
  public static <T extends Object> T[] concat(T[] array, @ParametricNullness T element) {
    T[] result = Arrays.copyOf(array, array.length + 1);
    result[array.length] = element;
    return result;
  }

  /**
   * Returns an array containing all of the elements in the specified collection; the runtime type
   * of the returned array is that of the specified array. If the collection fits in the specified
   * array, it is returned therein. Otherwise, a new array is allocated with the runtime type of the
   * specified array and the size of the specified collection.
   *
   * <p>If the collection fits in the specified array with room to spare (i.e., the array has more
   * elements than the collection), the element in the array immediately following the end of the
   * collection is set to {@code null}. This is useful in determining the length of the collection
   * <i>only</i> if the caller knows that the collection does not contain any null elements.
   *
   * <p>This method returns the elements in the order they are returned by the collection's
   * iterator.
   *
   * <p>TODO(kevinb): support concurrently modified collections?
   *
   * @param c the collection for which to return an array of elements
   * @param array the array in which to place the collection elements
   * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of
   *     the runtime type of every element in the specified collection
   */
  static <T extends Object> T[] toArrayImpl(Collection<?> c, T[] array) {
    int size = c.size();
    if (array.length < size) {
      array = newArray(array, size);
    }
    fillArray(c, array);
    if (array.length > size) {
      Object[] unsoundlyCovariantArray = array;
      unsoundlyCovariantArray[size] = null;
    }
    return array;
  }

  /**
   * Implementation of {@link Collection#toArray(Object[])} for collections backed by an object
   * array. the runtime type of the returned array is that of the specified array. If the collection
   * fits in the specified array, it is returned therein. Otherwise, a new array is allocated with
   * the runtime type of the specified array and the size of the specified collection.
   *
   * <p>If the collection fits in the specified array with room to spare (i.e., the array has more
   * elements than the collection), the element in the array immediately following the end of the
   * collection is set to {@code null}. This is useful in determining the length of the collection
   * <i>only</i> if the caller knows that the collection does not contain any null elements.
   */
  static <T extends Object> T[] toArrayImpl(
      Object[] src, int offset, int len, T[] dst) {
    checkPositionIndexes(offset, offset + len, src.length);
    if (dst.length < len) {
      dst = newArray(dst, len);
    } else if (dst.length > len) {
      Object[] unsoundlyCovariantArray = dst;
      unsoundlyCovariantArray[len] = null;
    }
    System.arraycopy(src, offset, dst, 0, len);
    return dst;
  }

  /**
   * Returns an array containing all of the elements in the specified collection. This method
   * returns the elements in the order they are returned by the collection's iterator. The returned
   * array is "safe" in that no references to it are maintained by the collection. The caller is
   * thus free to modify the returned array.
   *
   * <p>This method assumes that the collection size doesn't change while the method is running.
   *
   * <p>TODO(kevinb): support concurrently modified collections?
   *
   * @param c the collection for which to return an array of elements
   */
  static Object[] toArrayImpl(Collection<?> c) {
    return fillArray(c, new Object[c.size()]);
  }

  /**
   * Returns a copy of the specified subrange of the specified array that is literally an Object[],
   * and not e.g. a {@code String[]}.
   */
  static Object[] copyAsObjectArray(Object[] elements, int offset, int length) {
    checkPositionIndexes(offset, offset + length, elements.length);
    if (length == 0) {
      return new Object[0];
    }
    Object[] result = new Object[length];
    System.arraycopy(elements, offset, result, 0, length);
    return result;
  }

  
  private static Object[] fillArray(Iterable<?> elements, Object[] array) {
    int i = 0;
    for (Object element : elements) {
      array[i++] = element;
    }
    return array;
  }

  /** Swaps {@code array[i]} with {@code array[j]}. */
  static void swap(Object[] array, int i, int j) {
    Object temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  
  static Object[] checkElementsNotNull(Object... array) {
    return checkElementsNotNull(array, array.length);
  }

  
  static Object[] checkElementsNotNull(Object[] array, int length) {
    for (int i = 0; i < length; i++) {
      checkElementNotNull(array[i], i);
    }
    return array;
  }

  // We do this instead of Preconditions.checkNotNull to save boxing and array
  // creation cost.
  
  static Object checkElementNotNull(Object element, int index) {
    if (element == null) {
      throw new NullPointerException("at index " + index);
    }
    return element;
  }
}
