

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;




import java.util.Iterator;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;


/**
 * An iterator that transforms a backing iterator; for internal use. This avoids the object overhead
 * of constructing a {@link Function Function} for internal methods.
 *
 * @author Louis Wasserman
 */


abstract class TransformedIterator<F extends Object, T extends Object>
    implements Iterator<T> {
  final Iterator<? extends F> backingIterator;

  TransformedIterator(Iterator<? extends F> backingIterator) {
    this.backingIterator = checkNotNull(backingIterator);
  }

  @ParametricNullness
  abstract T transform(@ParametricNullness F from);

  @Override
  public final boolean hasNext() {
    return backingIterator.hasNext();
  }

  @Override
  @ParametricNullness
  public final T next() {
    return transform(backingIterator.next());
  }

  @Override
  public final void remove() {
    backingIterator.remove();
  }
}
