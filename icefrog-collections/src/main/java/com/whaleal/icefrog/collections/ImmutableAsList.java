

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * List returned by {@link ImmutableCollection#asList} that delegates {@code contains} checks to the
 * backing collection.
 *
 * @author Jared Levy
 * @author Louis Wasserman
 */

@SuppressWarnings("serial")

abstract class ImmutableAsList<E> extends ImmutableList<E> {
  abstract ImmutableCollection<E> delegateCollection();

  @Override
  public boolean contains(@CheckForNull Object target) {
    // The collection's contains() is at least as fast as ImmutableList's
    // and is often faster.
    return delegateCollection().contains(target);
  }

  @Override
  public int size() {
    return delegateCollection().size();
  }

  @Override
  public boolean isEmpty() {
    return delegateCollection().isEmpty();
  }

  @Override
  boolean isPartialView() {
    return delegateCollection().isPartialView();
  }

  /** Serialized form that leads to the same performance as the original list. */
 // serialization
  static class SerializedForm implements Serializable {
    final ImmutableCollection<?> collection;

    SerializedForm(ImmutableCollection<?> collection) {
      this.collection = collection;
    }

    Object readResolve() {
      return collection.asList();
    }

    private static final long serialVersionUID = 0;
  }

 // serialization
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }

 // serialization
  @Override
  Object writeReplace() {
    return new SerializedForm(delegateCollection());
  }
}
