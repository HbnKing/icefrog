/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.SpliteratorUtil;
import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Spliterator;

/**
 * List returned by {@code ImmutableSortedSet.asList()} when the set isn't empty.
 *
 * @author Jared Levy
 * @author Louis Wasserman
 */

@SuppressWarnings("serial")

final class ImmutableSortedAsList<E> extends RegularImmutableAsList<E>
    implements SortedIterable<E> {
  ImmutableSortedAsList(ImmutableSortedSet<E> backingSet, ImmutableList<E> backingList) {
    super(backingSet, backingList);
  }

  @Override
  ImmutableSortedSet<E> delegateCollection() {
    return (ImmutableSortedSet<E>) super.delegateCollection();
  }

  @Override
  public Comparator<? super E> comparator() {
    return delegateCollection().comparator();
  }

  // Override indexOf() and lastIndexOf() to be O(log N) instead of O(N).

 // ImmutableSortedSet.indexOf
  // TODO(cpovirk): consider manual binary search under GWT to preserve O(log N) lookup
  @Override
  public int indexOf(@CheckForNull Object target) {
    int index = delegateCollection().indexOf(target);

    // TODO(kevinb): reconsider if it's really worth making feeble attempts at
    // sanity for inconsistent comparators.

    // The equals() check is needed when the comparator isn't compatible with
    // equals().
    return (index >= 0 && get(index).equals(target)) ? index : -1;
  }

 // ImmutableSortedSet.indexOf
  @Override
  public int lastIndexOf(@CheckForNull Object target) {
    return indexOf(target);
  }

  @Override
  public boolean contains(@CheckForNull Object target) {
    // Necessary for ISS's with comparators inconsistent with equals.
    return indexOf(target) >= 0;
  }

 // super.subListUnchecked does not exist; inherited subList is valid if slow
  /*
   * TODO(cpovirk): if we start to override indexOf/lastIndexOf under GWT, we'll want some way to
   * override subList to return an ImmutableSortedAsList for better performance. Right now, I'm not
   * sure there's any performance hit from our failure to override subListUnchecked under GWT
   */
  @Override
  ImmutableList<E> subListUnchecked(int fromIndex, int toIndex) {
    ImmutableList<E> parentSubList = super.subListUnchecked(fromIndex, toIndex);
    return new RegularImmutableSortedSet<E>(parentSubList, comparator()).asList();
  }

  @Override
  public Spliterator<E> spliterator() {
    return SpliteratorUtil.indexed(
        size(),
        ImmutableList.SPLITERATOR_CHARACTERISTICS | Spliterator.SORTED | Spliterator.DISTINCT,
        delegateList()::get,
        comparator());
  }
}
