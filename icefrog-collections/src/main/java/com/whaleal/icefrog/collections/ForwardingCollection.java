

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;


import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Iterator;


/**
 * A collection which forwards all its method calls to another collection. Subclasses should
 * override one or more methods to modify the behavior of the backing collection as desired per the
 * <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingCollection} forward <b>indiscriminately</b> to
 * the methods of the delegate. For example, overriding {@link #add} alone <b>will not</b> change
 * the behavior of {@link #addAll}, which can lead to unexpected behavior. In this case, you should
 * override {@code addAll} as well, either providing your own implementation, or delegating to the
 * provided {@code standardAddAll} method.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingCollection}.
 *
 * <p>The {@code standard} methods are not guaranteed to be thread-safe, even when all of the
 * methods that they depend on are thread-safe.
 *
 * @author Kevin Bourrillion
 * @author Louis Wasserman
 * 
 */


public abstract class ForwardingCollection<E extends Object> extends ForwardingObject
    implements Collection<E> {
  // TODO(lowasser): identify places where thread safety is actually lost

  /** Constructor for use by subclasses. */
  protected ForwardingCollection() {}

  @Override
  protected abstract Collection<E> delegate();

  @Override
  public Iterator<E> iterator() {
    return delegate().iterator();
  }

  @Override
  public int size() {
    return delegate().size();
  }

  
  @Override
  public boolean removeAll(Collection<?> collection) {
    return delegate().removeAll(collection);
  }

  @Override
  public boolean isEmpty() {
    return delegate().isEmpty();
  }

  @Override
  public boolean contains(@CheckForNull Object object) {
    return delegate().contains(object);
  }

  
  @Override
  public boolean add(@ParametricNullness E element) {
    return delegate().add(element);
  }

  
  @Override
  public boolean remove(@CheckForNull Object object) {
    return delegate().remove(object);
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return delegate().containsAll(collection);
  }

  
  @Override
  public boolean addAll(Collection<? extends E> collection) {
    return delegate().addAll(collection);
  }

  
  @Override
  public boolean retainAll(Collection<?> collection) {
    return delegate().retainAll(collection);
  }

  @Override
  public void clear() {
    delegate().clear();
  }

  @Override
  public Object[] toArray() {
    return delegate().toArray();
  }

  
  @Override
  @SuppressWarnings("nullness") // b/192354773 in our checker affects toArray declarations
  public <T extends Object> T[] toArray(T[] array) {
    return delegate().toArray(array);
  }

  /**
   * A sensible definition of {@link #contains} in terms of {@link #iterator}. If you override
   * {@link #iterator}, you may wish to override {@link #contains} to forward to this
   * implementation.
   *
   * 
   */
  protected boolean standardContains(@CheckForNull Object object) {
    return Iterators.contains(iterator(), object);
  }

  /**
   * A sensible definition of {@link #containsAll} in terms of {@link #contains} . If you override
   * {@link #contains}, you may wish to override {@link #containsAll} to forward to this
   * implementation.
   *
   * 
   */
  protected boolean standardContainsAll(Collection<?> collection) {
    return Collections2.containsAllImpl(this, collection);
  }

  /**
   * A sensible definition of {@link #addAll} in terms of {@link #add}. If you override {@link
   * #add}, you may wish to override {@link #addAll} to forward to this implementation.
   *
   * 
   */
  protected boolean standardAddAll(Collection<? extends E> collection) {
    return Iterators.addAll(this, collection.iterator());
  }

  /**
   * A sensible definition of {@link #remove} in terms of {@link #iterator}, using the iterator's
   * {@code remove} method. If you override {@link #iterator}, you may wish to override {@link
   * #remove} to forward to this implementation.
   *
   * 
   */
  protected boolean standardRemove(@CheckForNull Object object) {
    Iterator<E> iterator = iterator();
    while (iterator.hasNext()) {
      if (ObjectUtil.equal(iterator.next(), object)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  /**
   * A sensible definition of {@link #removeAll} in terms of {@link #iterator}, using the iterator's
   * {@code remove} method. If you override {@link #iterator}, you may wish to override {@link
   * #removeAll} to forward to this implementation.
   *
   * 
   */
  protected boolean standardRemoveAll(Collection<?> collection) {
    return Iterators.removeAll(iterator(), collection);
  }

  /**
   * A sensible definition of {@link #retainAll} in terms of {@link #iterator}, using the iterator's
   * {@code remove} method. If you override {@link #iterator}, you may wish to override {@link
   * #retainAll} to forward to this implementation.
   *
   * 
   */
  protected boolean standardRetainAll(Collection<?> collection) {
    return Iterators.retainAll(iterator(), collection);
  }

  /**
   * A sensible definition of {@link #clear} in terms of {@link #iterator}, using the iterator's
   * {@code remove} method. If you override {@link #iterator}, you may wish to override {@link
   * #clear} to forward to this implementation.
   *
   * 
   */
  protected void standardClear() {
    Iterators.clear(iterator());
  }

  /**
   * A sensible definition of {@link #isEmpty} as {@code !iterator().hasNext}. If you override
   * {@link #isEmpty}, you may wish to override {@link #isEmpty} to forward to this implementation.
   * Alternately, it may be more efficient to implement {@code isEmpty} as {@code size() == 0}.
   *
   * 
   */
  protected boolean standardIsEmpty() {
    return !iterator().hasNext();
  }

  /**
   * A sensible definition of {@link #toString} in terms of {@link #iterator}. If you override
   * {@link #iterator}, you may wish to override {@link #toString} to forward to this
   * implementation.
   *
   * 
   */
  protected String standardToString() {
    return Collections2.toStringImpl(this);
  }

  /**
   * A sensible definition of {@link #toArray()} in terms of {@link #toArray(Object[])}. If you
   * override {@link #toArray(Object[])}, you may wish to override {@link #toArray} to forward to
   * this implementation.
   *
   * 
   */
  protected Object[] standardToArray() {
    Object[] newArray = new Object[size()];
    return toArray(newArray);
  }

  /**
   * A sensible definition of {@link #toArray(Object[])} in terms of {@link #size} and {@link
   * #iterator}. If you override either of these methods, you may wish to override {@link #toArray}
   * to forward to this implementation.
   *
   * 
   */
  protected <T extends Object> T[] standardToArray(T[] array) {
    return ObjectArrays.toArrayImpl(this, array);
  }
}
