

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;




/**
 * A sorted multiset which forwards all its method calls to another sorted multiset. Subclasses
 * should override one or more methods to modify the behavior of the backing multiset as desired per
 * the <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingSortedMultiset} forward
 * <b>indiscriminately</b> to the methods of the delegate. For example, overriding {@link
 * #add(Object, int)} alone <b>will not</b> change the behavior of {@link #add(Object)}, which can
 * lead to unexpected behavior. In this case, you should override {@code add(Object)} as well,
 * either providing your own implementation, or delegating to the provided {@code standardAdd}
 * method.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingSortedMultiset}.
 *
 * <p>The {@code standard} methods and any collection views they return are not guaranteed to be
 * thread-safe, even when all of the methods that they depend on are thread-safe.
 *
 * @author Louis Wasserman
 * 
 */



public abstract class ForwardingSortedMultiset<E extends Object>
    extends ForwardingMultiset<E> implements SortedMultiset<E> {
  /** Constructor for use by subclasses. */
  protected ForwardingSortedMultiset() {}

  @Override
  protected abstract SortedMultiset<E> delegate();

  @Override
  public NavigableSet<E> elementSet() {
    return delegate().elementSet();
  }

  /**
   * A sensible implementation of {@link SortedMultiset#elementSet} in terms of the following
   * methods: {@link SortedMultiset#clear}, {@link SortedMultiset#comparator}, {@link
   * SortedMultiset#contains}, {@link SortedMultiset#containsAll}, {@link SortedMultiset#count},
   * {@link SortedMultiset#firstEntry} {@link SortedMultiset#headMultiset}, {@link
   * SortedMultiset#isEmpty}, {@link SortedMultiset#lastEntry}, {@link SortedMultiset#subMultiset},
   * {@link SortedMultiset#tailMultiset}, the {@code size()} and {@code iterator()} methods of
   * {@link SortedMultiset#entrySet}, and {@link SortedMultiset#remove(Object, int)}. In many
   * situations, you may wish to override {@link SortedMultiset#elementSet} to forward to this
   * implementation or a subclass thereof.
   *
   * 
   */
  protected class StandardElementSet extends SortedMultisets.NavigableElementSet<E> {
    /** Constructor for use by subclasses. */
    public StandardElementSet() {
      super(ForwardingSortedMultiset.this);
    }
  }

  @Override
  public Comparator<? super E> comparator() {
    return delegate().comparator();
  }

  @Override
  public SortedMultiset<E> descendingMultiset() {
    return delegate().descendingMultiset();
  }

  /**
   * A skeleton implementation of a descending multiset view. Normally, {@link
   * #descendingMultiset()} will not reflect any changes you make to the behavior of methods such as
   * {@link #add(Object)} or {@link #pollFirstEntry}. This skeleton implementation correctly
   * delegates each of its operations to the appropriate methods of this {@code
   * ForwardingSortedMultiset}.
   *
   * <p>In many cases, you may wish to override {@link #descendingMultiset()} to return an instance
   * of a subclass of {@code StandardDescendingMultiset}.
   *
   * 
   */
  protected abstract class StandardDescendingMultiset extends DescendingMultiset<E> {
    /** Constructor for use by subclasses. */
    public StandardDescendingMultiset() {}

    @Override
    SortedMultiset<E> forwardMultiset() {
      return ForwardingSortedMultiset.this;
    }
  }

  @Override
  @CheckForNull
  public Entry<E> firstEntry() {
    return delegate().firstEntry();
  }

  /**
   * A sensible definition of {@link #firstEntry()} in terms of {@code entrySet().iterator()}.
   *
   * <p>If you override {@link #entrySet()}, you may wish to override {@link #firstEntry()} to
   * forward to this implementation.
   */
  @CheckForNull
  protected Entry<E> standardFirstEntry() {
    Iterator<Entry<E>> entryIterator = entrySet().iterator();
    if (!entryIterator.hasNext()) {
      return null;
    }
    Entry<E> entry = entryIterator.next();
    return Multisets.immutableEntry(entry.getElement(), entry.getCount());
  }

  @Override
  @CheckForNull
  public Entry<E> lastEntry() {
    return delegate().lastEntry();
  }

  /**
   * A sensible definition of {@link #lastEntry()} in terms of {@code
   * descendingMultiset().entrySet().iterator()}.
   *
   * <p>If you override {@link #descendingMultiset} or {@link #entrySet()}, you may wish to override
   * {@link #firstEntry()} to forward to this implementation.
   */
  @CheckForNull
  protected Entry<E> standardLastEntry() {
    Iterator<Entry<E>> entryIterator = descendingMultiset().entrySet().iterator();
    if (!entryIterator.hasNext()) {
      return null;
    }
    Entry<E> entry = entryIterator.next();
    return Multisets.immutableEntry(entry.getElement(), entry.getCount());
  }

  @Override
  @CheckForNull
  public Entry<E> pollFirstEntry() {
    return delegate().pollFirstEntry();
  }

  /**
   * A sensible definition of {@link #pollFirstEntry()} in terms of {@code entrySet().iterator()}.
   *
   * <p>If you override {@link #entrySet()}, you may wish to override {@link #pollFirstEntry()} to
   * forward to this implementation.
   */
  @CheckForNull
  protected Entry<E> standardPollFirstEntry() {
    Iterator<Entry<E>> entryIterator = entrySet().iterator();
    if (!entryIterator.hasNext()) {
      return null;
    }
    Entry<E> entry = entryIterator.next();
    entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
    entryIterator.remove();
    return entry;
  }

  @Override
  @CheckForNull
  public Entry<E> pollLastEntry() {
    return delegate().pollLastEntry();
  }

  /**
   * A sensible definition of {@link #pollLastEntry()} in terms of {@code
   * descendingMultiset().entrySet().iterator()}.
   *
   * <p>If you override {@link #descendingMultiset()} or {@link #entrySet()}, you may wish to
   * override {@link #pollLastEntry()} to forward to this implementation.
   */
  @CheckForNull
  protected Entry<E> standardPollLastEntry() {
    Iterator<Entry<E>> entryIterator = descendingMultiset().entrySet().iterator();
    if (!entryIterator.hasNext()) {
      return null;
    }
    Entry<E> entry = entryIterator.next();
    entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
    entryIterator.remove();
    return entry;
  }

  @Override
  public SortedMultiset<E> headMultiset(@ParametricNullness E upperBound, BoundType boundType) {
    return delegate().headMultiset(upperBound, boundType);
  }

  @Override
  public SortedMultiset<E> subMultiset(
      @ParametricNullness E lowerBound,
      BoundType lowerBoundType,
      @ParametricNullness E upperBound,
      BoundType upperBoundType) {
    return delegate().subMultiset(lowerBound, lowerBoundType, upperBound, upperBoundType);
  }

  /**
   * A sensible definition of {@link #subMultiset(Object, BoundType, Object, BoundType)} in terms of
   * {@link #headMultiset(Object, BoundType) headMultiset} and {@link #tailMultiset(Object,
   * BoundType) tailMultiset}.
   *
   * <p>If you override either of these methods, you may wish to override {@link
   * #subMultiset(Object, BoundType, Object, BoundType)} to forward to this implementation.
   */
  protected SortedMultiset<E> standardSubMultiset(
      @ParametricNullness E lowerBound,
      BoundType lowerBoundType,
      @ParametricNullness E upperBound,
      BoundType upperBoundType) {
    return tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
  }

  @Override
  public SortedMultiset<E> tailMultiset(@ParametricNullness E lowerBound, BoundType boundType) {
    return delegate().tailMultiset(lowerBound, boundType);
  }
}
