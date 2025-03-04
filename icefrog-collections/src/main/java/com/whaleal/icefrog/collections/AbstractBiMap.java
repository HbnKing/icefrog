

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static com.whaleal.icefrog.core.lang.Preconditions.checkArgument;
import static com.whaleal.icefrog.core.lang.Preconditions.checkState;


/**
 * A general-purpose bimap implementation using any two backing {@code Map} instances.
 *
 * <p>Note that this class contains {@code equals()} calls that keep it from supporting {@code
 * IdentityHashMap} backing MapUtil.
 *
 * @author Kevin Bourrillion
 * @author Mike Bostock
 */


abstract class AbstractBiMap<K extends Object, V extends Object>
    extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {

  private transient Map<K, V> delegate;
  transient AbstractBiMap<V, K> inverse;

  /**
     * Package-private constructor for creating a map-backed bimap.
     */
  AbstractBiMap(Map<K, V> forward, Map<V, K> backward) {
    setDelegates(forward, backward);
  }

  /**
     * Private constructor for inverse bimap.
     */
  private AbstractBiMap(Map<K, V> backward, AbstractBiMap<V, K> forward) {
    delegate = backward;
    inverse = forward;
  }

  @Override
  protected Map<K, V> delegate() {
    return delegate;
  }

  /**
     * Returns its input, or throws an exception if this is not a valid key.
     */

  @ParametricNullness
  K checkKey(@ParametricNullness K key) {
    return key;
  }

  /** Returns its input, or throws an exception if this is not a valid value. */

  @ParametricNullness
  V checkValue(@ParametricNullness V value) {
    return value;
  }

  /**
   * Specifies the delegate MapUtil going in each direction. Called by the constructor and by
   * subclasses during deserialization.
   */
  void setDelegates(Map<K, V> forward, Map<V, K> backward) {
    checkState(delegate == null);
    checkState(inverse == null);
    checkArgument(forward.isEmpty());
    checkArgument(backward.isEmpty());
    checkArgument(forward != backward);
    delegate = forward;
    inverse = makeInverse(backward);
  }

  AbstractBiMap<V, K> makeInverse(Map<V, K> backward) {
    return new Inverse<>(backward, this);
  }

  void setInverse(AbstractBiMap<V, K> inverse) {
    this.inverse = inverse;
  }

  // Query Operations (optimizations)

  @Override
  public boolean containsValue(@CheckForNull Object value) {
    return inverse.containsKey(value);
  }

  // Modification Operations


  @Override
  @CheckForNull
  public V put(@ParametricNullness K key, @ParametricNullness V value) {
    return putInBothMapUtil(key, value, false);
  }


  @Override
  @CheckForNull
  public V forcePut(@ParametricNullness K key, @ParametricNullness V value) {
    return putInBothMapUtil(key, value, true);
  }

  @CheckForNull
  private V putInBothMapUtil(@ParametricNullness K key, @ParametricNullness V value, boolean force) {
    checkKey(key);
    checkValue(value);
    boolean containedKey = containsKey(key);
    if (containedKey && com.whaleal.icefrog.core.util.ObjectUtil.equal(value, get(key))) {
      return value;
    }
    if (force) {
      inverse().remove(value);
    } else {
      checkArgument(!containsValue(value), "value already present: %s", value);
    }
    V oldValue = delegate.put(key, value);
    updateInverseMap(key, containedKey, oldValue, value);
    return oldValue;
  }

  private void updateInverseMap(
      @ParametricNullness K key,
      boolean containedKey,
      @CheckForNull V oldValue,
      @ParametricNullness V newValue) {
    if (containedKey) {
      // The cast is safe because of the containedKey check.
      removeFromInverseMap((oldValue));
    }
    inverse.delegate.put(newValue, key);
  }


  @Override
  @CheckForNull
  public V remove(@CheckForNull Object key) {
    return containsKey(key) ? removeFromBothMapUtil(key) : null;
  }


  @ParametricNullness
  private V removeFromBothMapUtil(@CheckForNull Object key) {
    // The cast is safe because the callers of this method first check that the key is present.
    V oldValue = (delegate.remove(key));
    removeFromInverseMap(oldValue);
    return oldValue;
  }

  private void removeFromInverseMap(@ParametricNullness V oldValue) {
    inverse.delegate.remove(oldValue);
  }

  // Bulk Operations

  @Override
  public void putAll(Map<? extends K, ? extends V> map) {
    for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    this.delegate.replaceAll(function);
    inverse.delegate.clear();
    Entry<K, V> broken = null;
    Iterator<Entry<K, V>> itr = this.delegate.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<K, V> entry = itr.next();
      K k = entry.getKey();
      V v = entry.getValue();
      K conflict = inverse.delegate.putIfAbsent(v, k);
      if (conflict != null) {
        broken = entry;
        // We're definitely going to throw, but we'll try to keep the BiMap in an internally
        // consistent state by removing the bad entry.
        itr.remove();
      }
    }
    if (broken != null) {
      throw new IllegalArgumentException("value already present: " + broken.getValue());
    }
  }

  @Override
  public void clear() {
    delegate.clear();
    inverse.delegate.clear();
  }

  // Views

  @Override
  public BiMap<V, K> inverse() {
    return inverse;
  }

  @CheckForNull private transient Set<K> keySet;

  @Override
  public Set<K> keySet() {
    Set<K> result = keySet;
    return (result == null) ? keySet = new KeySet() : result;
  }


  private class KeySet extends ForwardingSet<K> {
    @Override
    protected Set<K> delegate() {
      return delegate.keySet();
    }

    @Override
    public void clear() {
      AbstractBiMap.this.clear();
    }

    @Override
    public boolean remove(@CheckForNull Object key) {
      if (!contains(key)) {
        return false;
      }
      removeFromBothMapUtil(key);
      return true;
    }

    @Override
    public boolean removeAll(Collection<?> keysToRemove) {
      return standardRemoveAll(keysToRemove);
    }

    @Override
    public boolean retainAll(Collection<?> keysToRetain) {
      return standardRetainAll(keysToRetain);
    }

    @Override
    public Iterator<K> iterator() {
      return keySet().iterator();
    }
  }

  @CheckForNull private transient Set<V> valueSet;

  @Override
  public Set<V> values() {
    /*
     * We can almost reuse the inverse's keySet, except we have to fix the
     * iteration order so that it is consistent with the forward map.
     */
    Set<V> result = valueSet;
    return (result == null) ? valueSet = new ValueSet() : result;
  }


  private class ValueSet extends ForwardingSet<V> {
    final Set<V> valuesDelegate = inverse.keySet();

    @Override
    protected Set<V> delegate() {
      return valuesDelegate;
    }

    @Override
    public Iterator<V> iterator() {
      return Maps.valueIterator(entrySet().iterator());
    }

    @Override
    public Object[] toArray() {
      return standardToArray();
    }

    @Override
    @SuppressWarnings("nullness") // bug in our checker's handling of toArray signatures
    public <T extends Object> T[] toArray(T[] array) {
      return standardToArray(array);
    }

    @Override
    public String toString() {
      return standardToString();
    }
  }

  @CheckForNull private transient Set<Entry<K, V>> entrySet;

  @Override
  public Set<Entry<K, V>> entrySet() {
    Set<Entry<K, V>> result = entrySet;
    return (result == null) ? entrySet = new EntrySet() : result;
  }

  class BiMapEntry extends ForwardingMapEntry<K, V> {
    private final Entry<K, V> delegate;

    BiMapEntry(Entry<K, V> delegate) {
      this.delegate = delegate;
    }

    @Override
    protected Entry<K, V> delegate() {
      return delegate;
    }

    @Override
    public V setValue(V value) {
      checkValue(value);
      // Preconditions keep the map and inverse consistent.
      checkState(entrySet().contains(this), "entry no longer in map");
      // similar to putInBothMapUtil, but set via entry
      if (ObjectUtil.equal(value, getValue())) {
        return value;
      }
      checkArgument(!containsValue(value), "value already present: %s", value);
      V oldValue = delegate.setValue(value);
      checkState(com.whaleal.icefrog.core.util.ObjectUtil.equal(value, get(getKey())), "entry no longer in map");
      updateInverseMap(getKey(), true, oldValue, value);
      return oldValue;
    }
  }

  Iterator<Entry<K, V>> entrySetIterator() {
    final Iterator<Entry<K, V>> iterator = delegate.entrySet().iterator();
    return new Iterator<Entry<K, V>>() {
      @CheckForNull Entry<K, V> entry;

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public Entry<K, V> next() {
        entry = iterator.next();
        return new BiMapEntry(entry);
      }

      @Override
      public void remove() {
        if (entry == null) {
          throw new IllegalStateException("no calls to next() since the last call to remove()");
        }
        V value = entry.getValue();
        iterator.remove();
        removeFromInverseMap(value);
        entry = null;
      }
    };
  }


  private class EntrySet extends ForwardingSet<Entry<K, V>> {
    final Set<Entry<K, V>> esDelegate = delegate.entrySet();

    @Override
    protected Set<Entry<K, V>> delegate() {
      return esDelegate;
    }

    @Override
    public void clear() {
      AbstractBiMap.this.clear();
    }

    @Override
    public boolean remove(@CheckForNull Object object) {
      /*
       * `o instanceof Entry` is guaranteed by `contains`, but we check it here to satisfy our
       * nullness checker.
       */
      if (!esDelegate.contains(object) || !(object instanceof Entry)) {
        return false;
      }

      Entry<?, ?> entry = (Entry<?, ?>) object;
      inverse.delegate.remove(entry.getValue());
      /*
       * Remove the mapping in inverse before removing from esDelegate because
       * if entry is part of esDelegate, entry might be invalidated after the
       * mapping is removed from esDelegate.
       */
      esDelegate.remove(entry);
      return true;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return entrySetIterator();
    }

    // See java.util.Collections.CheckedEntrySet for details on attacks.

    @Override
    public Object[] toArray() {
      /*
       * standardToArray returns `Object[]` rather than `Object[]` but only because it can
       * be used with collections that may contain null. This collection never contains nulls, so we
       * can treat it as a plain `Object[]`.
       */
      @SuppressWarnings("nullness")
      Object[] result = standardToArray();
      return result;
    }

    @Override
    @SuppressWarnings("nullness") // bug in our checker's handling of toArray signatures
    public <T extends Object> T[] toArray(T[] array) {
      return standardToArray(array);
    }

    @Override
    public boolean contains(@CheckForNull Object o) {
      return Maps.containsEntryImpl(delegate(), o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      return standardContainsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      return standardRemoveAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      return standardRetainAll(c);
    }
  }

  /** The inverse of any other {@code AbstractBiMap} subclass. */
  static class Inverse<K extends Object, V extends Object>
      extends AbstractBiMap<K, V> {
    Inverse(Map<K, V> backward, AbstractBiMap<V, K> forward) {
      super(backward, forward);
    }

    /*
     * Serialization stores the forward bimap, the inverse of this inverse.
     * Deserialization calls inverse() on the forward bimap and returns that
     * inverse.
     *
     * If a bimap and its inverse are serialized together, the deserialized
     * instances have inverse() methods that return the other.
     */

    @Override
    @ParametricNullness
    K checkKey(@ParametricNullness K key) {
      return inverse.checkValue(key);
    }

    @Override
    @ParametricNullness
    V checkValue(@ParametricNullness V value) {
      return inverse.checkKey(value);
    }

    /** @serialData the forward bimap */
   // java.io.ObjectOutputStream
    private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeObject(inverse());
    }

   // java.io.ObjectInputStream
    @SuppressWarnings("unchecked") // reading data stored by writeObject
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      setInverse((AbstractBiMap<V, K>) stream.readObject());
    }

   // Not needed in the emulated source.
    Object readResolve() {
      return inverse().inverse();
    }

   // Not needed in emulated source.
    private static final long serialVersionUID = 0;
  }

 // Not needed in emulated source.
  private static final long serialVersionUID = 0;
}
