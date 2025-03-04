

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.Predicate;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.Map.Entry;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;
import static com.whaleal.icefrog.core.lang.Preconditions.checkPositionIndex;
import static com.whaleal.icefrog.core.map.MapUtil.keyPredicateOnEntries;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;


/**

 *
 * @author Louis Wasserman
 */


class FilteredKeyMultimap<K extends Object, V extends Object>
    extends AbstractMultimap<K, V> implements FilteredMultimap<K, V> {
  final Multimap<K, V> unfiltered;
  final Predicate<? super K> keyPredicate;

  FilteredKeyMultimap(Multimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    this.unfiltered = checkNotNull(unfiltered);
    this.keyPredicate = checkNotNull(keyPredicate);
  }

  @Override
  public Multimap<K, V> unfiltered() {
    return unfiltered;
  }

  @Override
  public Predicate<? super Entry<K, V>> entryPredicate() {
    return keyPredicateOnEntries(keyPredicate);
  }

  @Override
  public int size() {
    int size = 0;
    for (Collection<V> collection : asMap().values()) {
      size += collection.size();
    }
    return size;
  }

  @Override
  public boolean containsKey(@CheckForNull Object key) {
    if (unfiltered.containsKey(key)) {
      @SuppressWarnings("unchecked") // k is equal to a K, if not one itself
      K k = (K) key;
      return keyPredicate.apply(k);
    }
    return false;
  }

  @Override
  public Collection<V> removeAll(@CheckForNull Object key) {
    return containsKey(key) ? unfiltered.removeAll(key) : unmodifiableEmptyCollection();
  }

  Collection<V> unmodifiableEmptyCollection() {
    if (unfiltered instanceof SetMultimap) {
      return emptySet();
    } else {
      return emptyList();
    }
  }

  @Override
  public void clear() {
    keySet().clear();
  }

  @Override
  Set<K> createKeySet() {
    return Sets.filter(unfiltered.keySet(), keyPredicate);
  }

  @Override
  public Collection<V> get(@ParametricNullness K key) {
    if (keyPredicate.apply(key)) {
      return unfiltered.get(key);
    } else if (unfiltered instanceof SetMultimap) {
      return new AddRejectingSet<>(key);
    } else {
      return new AddRejectingList<>(key);
    }
  }

  static class AddRejectingSet<K extends Object, V extends Object>
      extends ForwardingSet<V> {
    @ParametricNullness final K key;

    AddRejectingSet(@ParametricNullness K key) {
      this.key = key;
    }

    @Override
    public boolean add(@ParametricNullness V element) {
      throw new IllegalArgumentException("Key does not satisfy predicate: " + key);
    }

    @Override
    public boolean addAll(Collection<? extends V> collection) {
      checkNotNull(collection);
      throw new IllegalArgumentException("Key does not satisfy predicate: " + key);
    }

    @Override
    protected Set<V> delegate() {
      return Collections.emptySet();
    }
  }

  static class AddRejectingList<K extends Object, V extends Object>
      extends ForwardingList<V> {
    @ParametricNullness final K key;

    AddRejectingList(@ParametricNullness K key) {
      this.key = key;
    }

    @Override
    public boolean add(@ParametricNullness V v) {
      add(0, v);
      return true;
    }

    @Override
    public void add(int index, @ParametricNullness V element) {
      checkPositionIndex(index, 0);
      throw new IllegalArgumentException("Key does not satisfy predicate: " + key);
    }

    @Override
    public boolean addAll(Collection<? extends V> collection) {
      addAll(0, collection);
      return true;
    }


    @Override
    public boolean addAll(int index, Collection<? extends V> elements) {
      checkNotNull(elements);
      checkPositionIndex(index, 0);
      throw new IllegalArgumentException("Key does not satisfy predicate: " + key);
    }

    @Override
    protected List<V> delegate() {
      return Collections.emptyList();
    }
  }

  @Override
  Iterator<Entry<K, V>> entryIterator() {
    throw new AssertionError("should never be called");
  }

  @Override
  Collection<Entry<K, V>> createEntries() {
    return new Entries();
  }


  class Entries extends ForwardingCollection<Entry<K, V>> {
    @Override
    protected Collection<Entry<K, V>> delegate() {
      return Collections2.filter(unfiltered.entries(), entryPredicate());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(@CheckForNull Object o) {
      if (o instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        if (unfiltered.containsKey(entry.getKey())
            // if this holds, then we know entry.getKey() is a K
            && keyPredicate.apply((K) entry.getKey())) {
          return unfiltered.remove(entry.getKey(), entry.getValue());
        }
      }
      return false;
    }
  }

  @Override
  Collection<V> createValues() {
    return new FilteredMultimapValues<>(this);
  }

  @Override
  Map<K, Collection<V>> createAsMap() {

    return Maps.filterKeys(unfiltered.asMap(), keyPredicate);
  }

  @Override
  Multiset<K> createKeys() {
    return Multisets.filter(unfiltered.keys(), keyPredicate);
  }
}
