

package com.whaleal.icefrog.collections;


import javax.annotation.CheckForNull;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collector;

import static com.whaleal.icefrog.collections.ObjectArrays.checkElementsNotNull;
import static com.whaleal.icefrog.core.lang.Preconditions.checkArgument;
import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;


/**
 * A {@link NavigableSet} whose contents will never change, with many other important properties
 * detailed at {@link ImmutableCollection}.
 *
 * <p><b>Warning:</b> as with any sorted collection, you are strongly advised not to use a {@link
 * Comparator} or {@link Comparable} type whose comparison behavior is <i>inconsistent with
 * equals</i>. That is, {@code a.compareTo(b)} or {@code comparator.compare(a, b)} should equal zero
 * <i>if and only if</i> {@code a.equals(b)}. If this advice is not followed, the resulting
 * collection will not correctly obey its specification.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/ImmutableCollectionsExplained"> immutable collections</a>.
 *
 * @author Jared Levy
 * @author Louis Wasserman
 * 
 */
// TODO(benyu): benchmark and optimize all creation paths, which are a mess now

@SuppressWarnings("serial") // we're overriding default serialization

public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E>
    implements NavigableSet<E>, SortedIterable<E> {
  static final int SPLITERATOR_CHARACTERISTICS =
      ImmutableSet.SPLITERATOR_CHARACTERISTICS | Spliterator.SORTED;

  /**
   * Returns a {@code Collector} that accumulates the input elements into a new {@code
   * ImmutableSortedSet}, ordered by the specified comparator.
   *
   * <p>If the elements contain duplicates (according to the comparator), only the first duplicate
   * in encounter order will appear in the result.
   *
   * 
   */
  public static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(
      Comparator<? super E> comparator) {
    return CollectCollectors.toImmutableSortedSet(comparator);
  }

  static <E> RegularImmutableSortedSet<E> emptySet(Comparator<? super E> comparator) {
    if (Ordering.natural().equals(comparator)) {
      return (RegularImmutableSortedSet<E>) RegularImmutableSortedSet.NATURAL_EMPTY_SET;
    } else {
      return new RegularImmutableSortedSet<E>(ImmutableList.of(), comparator);
    }
  }

  /**
   * Returns the empty immutable sorted set.
   *
   * <p><b>Performance note:</b> the instance returned is a singleton.
   */
  public static <E> ImmutableSortedSet<E> of() {
    return (ImmutableSortedSet<E>) RegularImmutableSortedSet.NATURAL_EMPTY_SET;
  }

  /** Returns an immutable sorted set containing a single element. */
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E element) {
    return new RegularImmutableSortedSet<E>(ImmutableList.of(element), Ordering.natural());
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@link Comparable#compareTo}, only the first
   * one specified is included.
   *
   * @throws NullPointerException if any element is null
   */
  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2) {
    return construct(Ordering.natural(), 2, e1, e2);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@link Comparable#compareTo}, only the first
   * one specified is included.
   *
   * @throws NullPointerException if any element is null
   */
  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3) {
    return construct(Ordering.natural(), 3, e1, e2, e3);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@link Comparable#compareTo}, only the first
   * one specified is included.
   *
   * @throws NullPointerException if any element is null
   */
  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4) {
    return construct(Ordering.natural(), 4, e1, e2, e3, e4);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@link Comparable#compareTo}, only the first
   * one specified is included.
   *
   * @throws NullPointerException if any element is null
   */
  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(
      E e1, E e2, E e3, E e4, E e5) {
    return construct(Ordering.natural(), 5, e1, e2, e3, e4, e5);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@link Comparable#compareTo}, only the first
   * one specified is included.
   *
   * @throws NullPointerException if any element is null
   * 
   */
  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(
      E e1, E e2, E e3, E e4, E e5, E e6, E... remaining) {
    Comparable[] contents = new Comparable[6 + remaining.length];
    contents[0] = e1;
    contents[1] = e2;
    contents[2] = e3;
    contents[3] = e4;
    contents[4] = e5;
    contents[5] = e6;
    System.arraycopy(remaining, 0, contents, 6, remaining.length);
    return construct(Ordering.natural(), contents.length, (E[]) contents);
  }

  // TODO(kevinb): Consider factory methods that reject duplicates

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@link Comparable#compareTo}, only the first
   * one specified is included.
   *
   * @throws NullPointerException if any of {@code elements} is null
   * 
   */
  public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] elements) {
    return construct(Ordering.natural(), elements.length, elements.clone());
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@code compareTo()}, only the first one
   * specified is included. To create a copy of a {@code SortedSet} that preserves the comparator,
   * call {@link #copyOfSorted} instead. This method iterates over {@code elements} at most once.
   *
   * <p>Note that if {@code s} is a {@code Set<String>}, then {@code ImmutableSortedSet.copyOf(s)}
   * returns an {@code ImmutableSortedSet<String>} containing each of the strings in {@code s},
   * while {@code ImmutableSortedSet.of(s)} returns an {@code ImmutableSortedSet<Set<String>>}
   * containing one element (the given set itself).
   *
   * <p>Despite the method name, this method attempts to avoid actually copying the data when it is
   * safe to do so. The exact circumstances under which a copy will or will not be performed are
   * undocumented and subject to change.
   *
   * <p>This method is not type-safe, as it may be called on elements that are not mutually
   * comparable.
   *
   * @throws ClassCastException if the elements are not mutually comparable
   * @throws NullPointerException if any of {@code elements} is null
   */
  public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> elements) {
    // Hack around E not being a subtype of Comparable.
    // Unsafe, see ImmutableSortedSetFauxverideShim.
    @SuppressWarnings("unchecked")
    Ordering<E> naturalOrder = (Ordering<E>) Ordering.natural();
    return copyOf(naturalOrder, elements);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@code compareTo()}, only the first one
   * specified is included. To create a copy of a {@code SortedSet} that preserves the comparator,
   * call {@link #copyOfSorted} instead. This method iterates over {@code elements} at most once.
   *
   * <p>Note that if {@code s} is a {@code Set<String>}, then {@code ImmutableSortedSet.copyOf(s)}
   * returns an {@code ImmutableSortedSet<String>} containing each of the strings in {@code s},
   * while {@code ImmutableSortedSet.of(s)} returns an {@code ImmutableSortedSet<Set<String>>}
   * containing one element (the given set itself).
   *
   * <p><b>Note:</b> Despite what the method name suggests, if {@code elements} is an {@code
   * ImmutableSortedSet}, it may be returned instead of a copy.
   *
   * <p>This method is not type-safe, as it may be called on elements that are not mutually
   * comparable.
   *
   * <p>This method is safe to use even when {@code elements} is a synchronized or concurrent
   * collection that is currently being modified by another thread.
   *
   * @throws ClassCastException if the elements are not mutually comparable
   * @throws NullPointerException if any of {@code elements} is null
   * 
   */
  public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> elements) {
    // Hack around E not being a subtype of Comparable.
    // Unsafe, see ImmutableSortedSetFauxverideShim.
    @SuppressWarnings("unchecked")
    Ordering<E> naturalOrder = (Ordering<E>) Ordering.natural();
    return copyOf(naturalOrder, elements);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by their natural ordering.
   * When multiple elements are equivalent according to {@code compareTo()}, only the first one
   * specified is included.
   *
   * <p>This method is not type-safe, as it may be called on elements that are not mutually
   * comparable.
   *
   * @throws ClassCastException if the elements are not mutually comparable
   * @throws NullPointerException if any of {@code elements} is null
   */
  public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> elements) {
    // Hack around E not being a subtype of Comparable.
    // Unsafe, see ImmutableSortedSetFauxverideShim.
    @SuppressWarnings("unchecked")
    Ordering<E> naturalOrder = (Ordering<E>) Ordering.natural();
    return copyOf(naturalOrder, elements);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by the given {@code
   * Comparator}. When multiple elements are equivalent according to {@code compareTo()}, only the
   * first one specified is included.
   *
   * @throws NullPointerException if {@code comparator} or any of {@code elements} is null
   */
  public static <E> ImmutableSortedSet<E> copyOf(
      Comparator<? super E> comparator, Iterator<? extends E> elements) {
    return new Builder<E>(comparator).addAll(elements).build();
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by the given {@code
   * Comparator}. When multiple elements are equivalent according to {@code compare()}, only the
   * first one specified is included. This method iterates over {@code elements} at most once.
   *
   * <p>Despite the method name, this method attempts to avoid actually copying the data when it is
   * safe to do so. The exact circumstances under which a copy will or will not be performed are
   * undocumented and subject to change.
   *
   * @throws NullPointerException if {@code comparator} or any of {@code elements} is null
   */
  public static <E> ImmutableSortedSet<E> copyOf(
      Comparator<? super E> comparator, Iterable<? extends E> elements) {
    checkNotNull(comparator);
    boolean hasSameComparator = SortedIterables.hasSameComparator(comparator, elements);

    if (hasSameComparator && (elements instanceof ImmutableSortedSet)) {
      @SuppressWarnings("unchecked")
      ImmutableSortedSet<E> original = (ImmutableSortedSet<E>) elements;
      if (!original.isPartialView()) {
        return original;
      }
    }
    @SuppressWarnings("unchecked") // elements only contains E's; it's safe.
    E[] array = (E[]) Iterables.toArray(elements);
    return construct(comparator, array.length, array);
  }

  /**
   * Returns an immutable sorted set containing the given elements sorted by the given {@code
   * Comparator}. When multiple elements are equivalent according to {@code compareTo()}, only the
   * first one specified is included.
   *
   * <p>Despite the method name, this method attempts to avoid actually copying the data when it is
   * safe to do so. The exact circumstances under which a copy will or will not be performed are
   * undocumented and subject to change.
   *
   * <p>This method is safe to use even when {@code elements} is a synchronized or concurrent
   * collection that is currently being modified by another thread.
   *
   * @throws NullPointerException if {@code comparator} or any of {@code elements} is null
   * 
   */
  public static <E> ImmutableSortedSet<E> copyOf(
      Comparator<? super E> comparator, Collection<? extends E> elements) {
    return copyOf(comparator, (Iterable<? extends E>) elements);
  }

  /**
   * Returns an immutable sorted set containing the elements of a sorted set, sorted by the same
   * {@code Comparator}. That behavior differs from {@link #copyOf(Iterable)}, which always uses the
   * natural ordering of the elements.
   *
   * <p>Despite the method name, this method attempts to avoid actually copying the data when it is
   * safe to do so. The exact circumstances under which a copy will or will not be performed are
   * undocumented and subject to change.
   *
   * <p>This method is safe to use even when {@code sortedSet} is a synchronized or concurrent
   * collection that is currently being modified by another thread.
   *
   * @throws NullPointerException if {@code sortedSet} or any of its elements is null
   */
  public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> sortedSet) {
    Comparator<? super E> comparator = SortedIterables.comparator(sortedSet);
    ImmutableList<E> list = ImmutableList.copyOf(sortedSet);
    if (list.isEmpty()) {
      return emptySet(comparator);
    } else {
      return new RegularImmutableSortedSet<E>(list, comparator);
    }
  }

  /**
   * Constructs an {@code ImmutableSortedSet} from the first {@code n} elements of {@code contents}.
   * If {@code k} is the size of the returned {@code ImmutableSortedSet}, then the sorted unique
   * elements are in the first {@code k} positions of {@code contents}, and {@code contents[i] ==
   * null} for {@code k <= i < n}.
   *
   * <p>If {@code k == contents.length}, then {@code contents} may no longer be safe for
   * modification.
   *
   * @throws NullPointerException if any of the first {@code n} elements of {@code contents} is null
   */
  static <E> ImmutableSortedSet<E> construct(
      Comparator<? super E> comparator, int n, E... contents) {
    if (n == 0) {
      return emptySet(comparator);
    }
    checkElementsNotNull(contents, n);
    Arrays.sort(contents, 0, n, comparator);
    int uniques = 1;
    for (int i = 1; i < n; i++) {
      E cur = contents[i];
      E prev = contents[uniques - 1];
      if (comparator.compare(cur, prev) != 0) {
        contents[uniques++] = cur;
      }
    }
    Arrays.fill(contents, uniques, n, null);
    return new RegularImmutableSortedSet<E>(
        ImmutableList.asImmutableList(contents, uniques), comparator);
  }

  /**
   * Returns a builder that creates immutable sorted sets with an explicit comparator. If the
   * comparator has a more general type than the set being generated, such as creating a {@code
   * SortedSet<Integer>} with a {@code Comparator<Number>}, use the {@link Builder} constructor
   * instead.
   *
   * @throws NullPointerException if {@code comparator} is null
   */
  public static <E> Builder<E> orderedBy(Comparator<E> comparator) {
    return new Builder<E>(comparator);
  }

  /**
   * Returns a builder that creates immutable sorted sets whose elements are ordered by the reverse
   * of their natural ordering.
   */
  public static <E extends Comparable<?>> Builder<E> reverseOrder() {
    return new Builder<E>(Collections.reverseOrder());
  }

  /**
   * Returns a builder that creates immutable sorted sets whose elements are ordered by their
   * natural ordering. The sorted sets use {@link Ordering#natural()} as the comparator. This method
   * provides more type-safety than {@link #builder}, as it can be called only for classes that
   * implement {@link Comparable}.
   */
  public static <E extends Comparable<?>> Builder<E> naturalOrder() {
    return new Builder<E>(Ordering.natural());
  }

  /**
   * A builder for creating immutable sorted set instances, especially {@code public static final}
   * sets ("constant sets"), with a given comparator. Example:
   *
   * <pre>{@code
   * public static final ImmutableSortedSet<Number> LUCKY_NUMBERS =
   *     new ImmutableSortedSet.Builder<Number>(ODDS_FIRST_COMPARATOR)
   *         .addAll(SINGLE_DIGIT_PRIMES)
   *         .add(42)
   *         .build();
   * }</pre>
   *
   * <p>Builder instances can be reused; it is safe to call {@link #build} multiple times to build
   * multiple sets in series. Each set is a superset of the set created before it.
   *
   * 
   */
  public static final class Builder<E> extends ImmutableSet.Builder<E> {
    private final Comparator<? super E> comparator;
    private E[] elements;
    private int n;

    /**
     * Creates a new builder. The returned builder is equivalent to the builder generated by {@link
     * ImmutableSortedSet#orderedBy}.
     */
    public Builder(Comparator<? super E> comparator) {
      super(true); // don't construct guts of hash-based set builder
      this.comparator = checkNotNull(comparator);
      this.elements = (E[]) new Object[ImmutableCollection.Builder.DEFAULT_INITIAL_CAPACITY];
      this.n = 0;
    }

    @Override
    void copy() {
      elements = Arrays.copyOf(elements, elements.length);
    }

    private void sortAndDedup() {
      if (n == 0) {
        return;
      }
      Arrays.sort(elements, 0, n, comparator);
      int unique = 1;
      for (int i = 1; i < n; i++) {
        int cmp = comparator.compare(elements[unique - 1], elements[i]);
        if (cmp < 0) {
          elements[unique++] = elements[i];
        } else if (cmp > 0) {
          throw new AssertionError(
              "Comparator " + comparator + " compare method violates its contract");
        }
      }
      Arrays.fill(elements, unique, n, null);
      n = unique;
    }

    /**
     * Adds {@code element} to the {@code ImmutableSortedSet}. If the {@code ImmutableSortedSet}
     * already contains {@code element}, then {@code add} has no effect. (only the previously added
     * element is retained).
     *
     * @param element the element to add
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code element} is null
     */

    @Override
    public Builder<E> add(E element) {
      checkNotNull(element);
      copyIfNecessary();
      if (n == elements.length) {
        sortAndDedup();
        /*
         * Sorting operations can only be allowed to occur once every O(n) operations to keep
         * amortized O(n log n) performance.  Therefore, ensure there are at least O(n) *unused*
         * spaces in the builder array.
         */
        int newLength = ImmutableCollection.Builder.expandedCapacity(n, n + 1);
        if (newLength > elements.length) {
          elements = Arrays.copyOf(elements, newLength);
        }
      }
      elements[n++] = element;
      return this;
    }

    /**
     * Adds each element of {@code elements} to the {@code ImmutableSortedSet}, ignoring duplicate
     * elements (only the first duplicate element is added).
     *
     * @param elements the elements to add
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code elements} contains a null element
     */

    @Override
    public Builder<E> add(E... elements) {
      checkElementsNotNull(elements);
      for (E e : elements) {
        add(e);
      }
      return this;
    }

    /**
     * Adds each element of {@code elements} to the {@code ImmutableSortedSet}, ignoring duplicate
     * elements (only the first duplicate element is added).
     *
     * @param elements the elements to add to the {@code ImmutableSortedSet}
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code elements} contains a null element
     */

    @Override
    public Builder<E> addAll(Iterable<? extends E> elements) {
      super.addAll(elements);
      return this;
    }

    /**
     * Adds each element of {@code elements} to the {@code ImmutableSortedSet}, ignoring duplicate
     * elements (only the first duplicate element is added).
     *
     * @param elements the elements to add to the {@code ImmutableSortedSet}
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code elements} contains a null element
     */

    @Override
    public Builder<E> addAll(Iterator<? extends E> elements) {
      super.addAll(elements);
      return this;
    }


    @Override
    Builder<E> combine(ImmutableSet.Builder<E> builder) {
      copyIfNecessary();
      Builder<E> other = (Builder<E>) builder;
      for (int i = 0; i < other.n; i++) {
        add(other.elements[i]);
      }
      return this;
    }

    /**
     * Returns a newly-created {@code ImmutableSortedSet} based on the contents of the {@code
     * Builder} and its comparator.
     */
    @Override
    public ImmutableSortedSet<E> build() {
      sortAndDedup();
      if (n == 0) {
        return emptySet(comparator);
      } else {
        forceCopy = true;
        return new RegularImmutableSortedSet<E>(
            ImmutableList.asImmutableList(elements, n), comparator);
      }
    }
  }

  int unsafeCompare(Object a, @CheckForNull Object b) {
    return unsafeCompare(comparator, a, b);
  }

  static int unsafeCompare(Comparator<?> comparator, Object a, @CheckForNull Object b) {
    // Pretend the comparator can compare anything. If it turns out it can't
    // compare a and b, we should get a CCE or NPE on the subsequent line. Only methods
    // that are spec'd to throw CCE and NPE should call this.
    @SuppressWarnings({"unchecked", "nullness"})
    Comparator<Object> unsafeComparator = (Comparator<Object>) comparator;
    return unsafeComparator.compare(a, b);
  }

  final transient Comparator<? super E> comparator;

  ImmutableSortedSet(Comparator<? super E> comparator) {
    this.comparator = comparator;
  }

  /**
   * Returns the comparator that orders the elements, which is {@link Ordering#natural()} when the
   * natural ordering of the elements is used. Note that its behavior is not consistent with {@link
   * SortedSet#comparator()}, which returns {@code null} to indicate natural ordering.
   */
  @Override
  public Comparator<? super E> comparator() {
    return comparator;
  }

  @Override // needed to unify the iterator() methods in Collection and SortedIterable
  public abstract Iterator<E> iterator();

  /**
   * {@inheritDoc}
   *
   * <p>This method returns a serializable {@code ImmutableSortedSet}.
   *
   * <p>The {@link SortedSet#headSet} documentation states that a subset of a subset throws an
   * {@link IllegalArgumentException} if passed a {@code toElement} greater than an earlier {@code
   * toElement}. However, this method doesn't throw an exception in that situation, but instead
   * keeps the original {@code toElement}.
   */
  @Override
  public ImmutableSortedSet<E> headSet(E toElement) {
    return headSet(toElement, false);
  }


  @Override
  public ImmutableSortedSet<E> headSet(E toElement, boolean inclusive) {
    return headSetImpl(checkNotNull(toElement), inclusive);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This method returns a serializable {@code ImmutableSortedSet}.
   *
   * <p>The {@link SortedSet#subSet} documentation states that a subset of a subset throws an {@link
   * IllegalArgumentException} if passed a {@code fromElement} smaller than an earlier {@code
   * fromElement}. However, this method doesn't throw an exception in that situation, but instead
   * keeps the original {@code fromElement}. Similarly, this method keeps the original {@code
   * toElement}, instead of throwing an exception, if passed a {@code toElement} greater than an
   * earlier {@code toElement}.
   */
  @Override
  public ImmutableSortedSet<E> subSet(E fromElement, E toElement) {
    return subSet(fromElement, true, toElement, false);
  }


 // NavigableSet
  @Override
  public ImmutableSortedSet<E> subSet(
      E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
    checkNotNull(fromElement);
    checkNotNull(toElement);
    checkArgument(comparator.compare(fromElement, toElement) <= 0);
    return subSetImpl(fromElement, fromInclusive, toElement, toInclusive);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This method returns a serializable {@code ImmutableSortedSet}.
   *
   * <p>The {@link SortedSet#tailSet} documentation states that a subset of a subset throws an
   * {@link IllegalArgumentException} if passed a {@code fromElement} smaller than an earlier {@code
   * fromElement}. However, this method doesn't throw an exception in that situation, but instead
   * keeps the original {@code fromElement}.
   */
  @Override
  public ImmutableSortedSet<E> tailSet(E fromElement) {
    return tailSet(fromElement, true);
  }


  @Override
  public ImmutableSortedSet<E> tailSet(E fromElement, boolean inclusive) {
    return tailSetImpl(checkNotNull(fromElement), inclusive);
  }

  /*
   * These methods perform most headSet, subSet, and tailSet logic, besides
   * parameter validation.
   */
  abstract ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive);

  abstract ImmutableSortedSet<E> subSetImpl(
      E fromElement, boolean fromInclusive, E toElement, boolean toInclusive);

  abstract ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive);


 // NavigableSet
  @Override
  @CheckForNull
  public E lower(E e) {
    return Iterators.getNext(headSet(e, false).descendingIterator(), null);
  }


  @Override
  @CheckForNull
  public E floor(E e) {
    return Iterators.getNext(headSet(e, true).descendingIterator(), null);
  }


  @Override
  @CheckForNull
  public E ceiling(E e) {
    return Iterables.getFirst(tailSet(e, true), null);
  }


 // NavigableSet
  @Override
  @CheckForNull
  public E higher(E e) {
    return Iterables.getFirst(tailSet(e, false), null);
  }

  @Override
  public E first() {
    return iterator().next();
  }

  @Override
  public E last() {
    return descendingIterator().next();
  }

  /**
   * Guaranteed to throw an exception and leave the set unmodified.
   *
   * 
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */

  @Deprecated
 // NavigableSet
  @Override

  @CheckForNull
  public final E pollFirst() {
    throw new UnsupportedOperationException();
  }

  /**
   * Guaranteed to throw an exception and leave the set unmodified.
   *
   * 
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */

  @Deprecated
 // NavigableSet
  @Override

  @CheckForNull
  public final E pollLast() {
    throw new UnsupportedOperationException();
  }

 // NavigableSet

  @CheckForNull
  transient ImmutableSortedSet<E> descendingSet;


 // NavigableSet
  @Override
  public ImmutableSortedSet<E> descendingSet() {
    // racy single-check idiom
    ImmutableSortedSet<E> result = descendingSet;
    if (result == null) {
      result = descendingSet = createDescendingSet();
      result.descendingSet = this;
    }
    return result;
  }

  // Most classes should implement this as new DescendingImmutableSortedSet<E>(this),
  // but we push down that implementation because ProGuard can't eliminate it even when it's always
  // overridden.
 // NavigableSet
  abstract ImmutableSortedSet<E> createDescendingSet();

  @Override
  public Spliterator<E> spliterator() {
    return new Spliterators.AbstractSpliterator<E>(
        size(), SPLITERATOR_CHARACTERISTICS | Spliterator.SIZED) {
      final Iterator<E> iterator = iterator();

      @Override
      public boolean tryAdvance(Consumer<? super E> action) {
        if (iterator.hasNext()) {
          action.accept(iterator.next());
          return true;
        } else {
          return false;
        }
      }

      @Override
      public Comparator<? super E> getComparator() {
        return comparator;
      }
    };
  }


  @Override
  public abstract Iterator<E> descendingIterator();

  /** Returns the position of an element within the set, or -1 if not present. */
  abstract int indexOf(@CheckForNull Object target);

  /*
   * This class is used to serialize all ImmutableSortedSet instances,
   * regardless of implementation type. It captures their "logical contents"
   * only. This is necessary to ensure that the existence of a particular
   * implementation type is an implementation detail.
   */
  private static class SerializedForm<E> implements Serializable {
    final Comparator<? super E> comparator;
    final Object[] elements;

    public SerializedForm(Comparator<? super E> comparator, Object[] elements) {
      this.comparator = comparator;
      this.elements = elements;
    }

    @SuppressWarnings("unchecked")
    Object readResolve() {
      return new Builder<E>(comparator).add((E[]) elements).build();
    }

    private static final long serialVersionUID = 0;
  }

  private void readObject(ObjectInputStream unused) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }

  @Override
  Object writeReplace() {
    return new SerializedForm<E>(comparator, toArray());
  }
}
