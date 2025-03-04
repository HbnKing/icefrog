

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * A multimap which forwards all its method calls to another multimap. Subclasses should override
 * one or more methods to modify the behavior of the backing multimap as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingMultimap}.
 *
 * @author Robert Konigsberg
 * 
 */


public abstract class ForwardingMultimap<K extends Object, V extends Object>
    extends ForwardingObject implements Multimap<K, V> {

  /** Constructor for use by subclasses. */
  protected ForwardingMultimap() {}

  @Override
  protected abstract Multimap<K, V> delegate();

  @Override
  public Map<K, Collection<V>> asMap() {
    return delegate().asMap();
  }

  @Override
  public void clear() {
    delegate().clear();
  }

  @Override
  public boolean containsEntry(@CheckForNull Object key, @CheckForNull Object value) {
    return delegate().containsEntry(key, value);
  }

  @Override
  public boolean containsKey(@CheckForNull Object key) {
    return delegate().containsKey(key);
  }

  @Override
  public boolean containsValue(@CheckForNull Object value) {
    return delegate().containsValue(value);
  }

  @Override
  public Collection<Entry<K, V>> entries() {
    return delegate().entries();
  }

  @Override
  public Collection<V> get(@ParametricNullness K key) {
    return delegate().get(key);
  }

  @Override
  public boolean isEmpty() {
    return delegate().isEmpty();
  }

  @Override
  public Multiset<K> keys() {
    return delegate().keys();
  }

  @Override
  public Set<K> keySet() {
    return delegate().keySet();
  }


  @Override
  public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
    return delegate().put(key, value);
  }


  @Override
  public boolean putAll(@ParametricNullness K key, Iterable<? extends V> values) {
    return delegate().putAll(key, values);
  }


  @Override
  public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
    return delegate().putAll(multimap);
  }


  @Override
  public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
    return delegate().remove(key, value);
  }


  @Override
  public Collection<V> removeAll(@CheckForNull Object key) {
    return delegate().removeAll(key);
  }


  @Override
  public Collection<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return delegate().replaceValues(key, values);
  }

  @Override
  public int size() {
    return delegate().size();
  }

  @Override
  public Collection<V> values() {
    return delegate().values();
  }

  @Override
  public boolean equals(@CheckForNull Object object) {
    return object == this || delegate().equals(object);
  }

  @Override
  public int hashCode() {
    return delegate().hashCode();
  }
}
