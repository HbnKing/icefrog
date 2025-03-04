

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.ListUtil;
import com.whaleal.icefrog.core.map.MapUtil;


import com.whaleal.icefrog.core.collection.CollUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import static com.whaleal.icefrog.core.lang.Preconditions.checkArgument;
import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;


/**
 * A {@link SortedMultiset} whose contents will never change, with many other important properties
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
 * @author Louis Wasserman
 * 
 */


public abstract class ImmutableSortedMultiset<E> extends ImmutableSortedMultisetFauxverideShim<E>
    implements SortedMultiset<E> {
  // TODO(lowasser): GWT compatibility

  /**
   * Returns a {@code Collector} that accumulates the input elements into a new {@code
   * ImmutableMultiset}. Elements are sorted by the specified comparator.
   *
   * <p><b>Warning:</b> {@code comparator} should be <i>consistent with {@code equals}</i> as
   * explained in the {@link Comparator} documentation.
   *
   * 
   */
  public static <E> Collector<E, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(
      Comparator<? super E> comparator) {
    return toImmutableSortedMultiset(comparator, Function.identity(), e -> 1);
  }

  /**
   * Returns a {@code Collector} that accumulates elements into an {@code ImmutableSortedMultiset}
   * whose elements are the result of applying {@code elementFunction} to the inputs, with counts
   * equal to the result of applying {@code countFunction} to the inputs.
   *
   * <p>If the mapped elements contain duplicates (according to {@code comparator}), the first
   * occurrence in encounter order appears in the resulting multiset, with count equal to the sum of
   * the outputs of {@code countFunction.applyAsInt(t)} for each {@code t} mapped to that element.
   *
   * 
   */
  public static <T extends Object, E>
      Collector<T, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(
          Comparator<? super E> comparator,
          Function<? super T, ? extends E> elementFunction,
          ToIntFunction<? super T> countFunction) {
    checkNotNull(comparator);
    checkNotNull(elementFunction);
    checkNotNull(countFunction);
    return Collector.of(
        () -> TreeMultiset.create(comparator),
        (multiset, t) ->
            multiset.add(checkNotNull(elementFunction.apply(t)), countFunction.applyAsInt(t)),
        (multiset1, multiset2) -> {
          multiset1.addAll(multiset2);
          return multiset1;
        },
        (Multiset<E> multiset) -> copyOfSortedEntries(comparator, multiset.entrySet()));
  }

  /**
   * Returns the empty immutable sorted multiset.
   *
   * <p><b>Performance note:</b> the instance returned is a singleton.
   */
  @SuppressWarnings("unchecked")
  public static <E> ImmutableSortedMultiset<E> of() {
    return (ImmutableSortedMultiset) RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
  }

  /** Returns an immutable sorted multiset containing a single element. */
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E element) {
    RegularImmutableSortedSet<E> elementSet =
        (RegularImmutableSortedSet<E>) ImmutableSortedSet.of(element);
    long[] cumulativeCounts = {0, 1};
    return new RegularImmutableSortedMultiset<E>(elementSet, cumulativeCounts, 0, 1);
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering.
   *
   * @throws NullPointerException if any element is null
   */
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2) {
    return copyOf(Ordering.natural(), Arrays.asList(e1, e2));
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering.
   *
   * @throws NullPointerException if any element is null
   */
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3) {
    return copyOf(Ordering.natural(), Arrays.asList(e1, e2, e3));
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering.
   *
   * @throws NullPointerException if any element is null
   */
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(
      E e1, E e2, E e3, E e4) {
    return copyOf(Ordering.natural(), Arrays.asList(e1, e2, e3, e4));
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering.
   *
   * @throws NullPointerException if any element is null
   */
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(
      E e1, E e2, E e3, E e4, E e5) {
    return copyOf(Ordering.natural(), Arrays.asList(e1, e2, e3, e4, e5));
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering.
   *
   * @throws NullPointerException if any element is null
   */
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(
      E e1, E e2, E e3, E e4, E e5, E e6, E... remaining) {
    int size = remaining.length + 6;
    List<E> all = new ArrayList(size);

    Collections.addAll(all, e1, e2, e3, e4, e5, e6);
    Collections.addAll(all, remaining);
    return copyOf(Ordering.natural(), all);
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering.
   *
   * @throws NullPointerException if any of {@code elements} is null
   */
  public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] elements) {
    return copyOf(Ordering.natural(), Arrays.asList(elements));
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering. To create a copy of a {@code SortedMultiset} that preserves the comparator, call
   * {@link #copyOfSorted} instead. This method iterates over {@code elements} at most once.
   *
   * <p>Note that if {@code s} is a {@code Multiset<String>}, then {@code
   * ImmutableSortedMultiset.copyOf(s)} returns an {@code ImmutableSortedMultiset<String>}
   * containing each of the strings in {@code s}, while {@code ImmutableSortedMultiset.of(s)}
   * returns an {@code ImmutableSortedMultiset<Multiset<String>>} containing one element (the given
   * multiset itself).
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
  public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> elements) {
    // Hack around E not being a subtype of Comparable.
    // Unsafe, see ImmutableSortedMultisetFauxverideShim.
    @SuppressWarnings("unchecked")
    Ordering<E> naturalOrder = (Ordering<E>) Ordering.natural();
    return copyOf(naturalOrder, elements);
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by their natural
   * ordering.
   *
   * <p>This method is not type-safe, as it may be called on elements that are not mutually
   * comparable.
   *
   * @throws ClassCastException if the elements are not mutually comparable
   * @throws NullPointerException if any of {@code elements} is null
   */
  public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> elements) {
    // Hack around E not being a subtype of Comparable.
    // Unsafe, see ImmutableSortedMultisetFauxverideShim.
    @SuppressWarnings("unchecked")
    Ordering<E> naturalOrder = (Ordering<E>) Ordering.natural();
    return copyOf(naturalOrder, elements);
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by the given {@code
   * Comparator}.
   *
   * @throws NullPointerException if {@code comparator} or any of {@code elements} is null
   */
  public static <E> ImmutableSortedMultiset<E> copyOf(
      Comparator<? super E> comparator, Iterator<? extends E> elements) {
    checkNotNull(comparator);
    return new Builder<E>(comparator).addAll(elements).build();
  }

  /**
   * Returns an immutable sorted multiset containing the given elements sorted by the given {@code
   * Comparator}. This method iterates over {@code elements} at most once.
   *
   * <p>Despite the method name, this method attempts to avoid actually copying the data when it is
   * safe to do so. The exact circumstances under which a copy will or will not be performed are
   * undocumented and subject to change.
   *
   * @throws NullPointerException if {@code comparator} or any of {@code elements} is null
   */
  public static <E> ImmutableSortedMultiset<E> copyOf(
      Comparator<? super E> comparator, Iterable<? extends E> elements) {
    if (elements instanceof ImmutableSortedMultiset) {
      @SuppressWarnings("unchecked") // immutable collections are always safe for covariant casts
      ImmutableSortedMultiset<E> multiset = (ImmutableSortedMultiset<E>) elements;
      if (comparator.equals(multiset.comparator())) {
        if (multiset.isPartialView()) {
          return copyOfSortedEntries(comparator, CollUtil.newArrayList(multiset.entrySet()));
        } else {
          return multiset;
        }
      }
    }
    elements = ListUtil.list(false,elements); // defensive copy
    TreeMultiset<E> sortedCopy = TreeMultiset.create(checkNotNull(comparator));
    Iterables.addAll(sortedCopy, elements);
    return copyOfSortedEntries(comparator, sortedCopy.entrySet());
  }

  /**
   * Returns an immutable sorted multiset containing the elements of a sorted multiset, sorted by
   * the same {@code Comparator}. That behavior differs from {@link #copyOf(Iterable)}, which always
   * uses the natural ordering of the elements.
   *
   * <p>Despite the method name, this method attempts to avoid actually copying the data when it is
   * safe to do so. The exact circumstances under which a copy will or will not be performed are
   * undocumented and subject to change.
   *
   * <p>This method is safe to use even when {@code sortedMultiset} is a synchronized or concurrent
   * collection that is currently being modified by another thread.
   *
   * @throws NullPointerException if {@code sortedMultiset} or any of its elements is null
   */
  public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> sortedMultiset) {
    return copyOfSortedEntries(
        sortedMultiset.comparator(), ListUtil.list(false,sortedMultiset.entrySet()));
  }

  private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(
      Comparator<? super E> comparator, Collection<Entry<E>> entries) {
    if (entries.isEmpty()) {
      return emptyMultiset(comparator);
    }
    ImmutableList.Builder<E> elementsBuilder = new ImmutableList.Builder<E>(entries.size());
    long[] cumulativeCounts = new long[entries.size() + 1];
    int i = 0;
    for (Entry<E> entry : entries) {
      elementsBuilder.add(entry.getElement());
      cumulativeCounts[i + 1] = cumulativeCounts[i] + entry.getCount();
      i++;
    }
    return new RegularImmutableSortedMultiset<E>(
        new RegularImmutableSortedSet<E>(elementsBuilder.build(), comparator),
        cumulativeCounts,
        0,
        entries.size());
  }

  @SuppressWarnings("unchecked")
  static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> comparator) {
    if (Ordering.natural().equals(comparator)) {
      return (ImmutableSortedMultiset<E>) RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
    } else {
      return new RegularImmutableSortedMultiset<E>(comparator);
    }
  }

  ImmutableSortedMultiset() {}

  @Override
  public final Comparator<? super E> comparator() {
    return elementSet().comparator();
  }

  @Override
  public abstract ImmutableSortedSet<E> elementSet();

   @CheckForNull transient ImmutableSortedMultiset<E> descendingMultiset;

  @Override
  public ImmutableSortedMultiset<E> descendingMultiset() {
    ImmutableSortedMultiset<E> result = descendingMultiset;
    if (result == null) {
      return descendingMultiset =
          this.isEmpty()
              ? emptyMultiset(Ordering.from(comparator()).reverse())
              : new DescendingImmutableSortedMultiset<E>(this);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation is guaranteed to throw an {@link UnsupportedOperationException}.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */

  @Deprecated
  @Override

  @CheckForNull
  public final Entry<E> pollFirstEntry() {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation is guaranteed to throw an {@link UnsupportedOperationException}.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */

  @Deprecated
  @Override

  @CheckForNull
  public final Entry<E> pollLastEntry() {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType);

  @Override
  public ImmutableSortedMultiset<E> subMultiset(
      E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType) {
    checkArgument(
        comparator().compare(lowerBound, upperBound) <= 0,
        "Expected lowerBound <= upperBound but %s > %s",
        lowerBound,
        upperBound);
    return tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
  }

  @Override
  public abstract ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType);

  /**
   * Returns a builder that creates immutable sorted multisets with an explicit comparator. If the
   * comparator has a more general type than the set being generated, such as creating a {@code
   * SortedMultiset<Integer>} with a {@code Comparator<Number>}, use the {@link Builder} constructor
   * instead.
   *
   * @throws NullPointerException if {@code comparator} is null
   */
  public static <E> Builder<E> orderedBy(Comparator<E> comparator) {
    return new Builder<E>(comparator);
  }

  /**
   * Returns a builder that creates immutable sorted multisets whose elements are ordered by the
   * reverse of their natural ordering.
   *
   * <p>Note: the type parameter {@code E} extends {@code Comparable<?>} rather than {@code
   * Comparable<? super E>} as a workaround for javac <a
   * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6468354">bug 6468354</a>.
   */
  public static <E extends Comparable<?>> Builder<E> reverseOrder() {
    return new Builder<E>(Ordering.natural().reverse());
  }

  /**
   * Returns a builder that creates immutable sorted multisets whose elements are ordered by their
   * natural ordering. The sorted multisets use {@link Ordering#natural()} as the comparator. This
   * method provides more type-safety than {@link #builder}, as it can be called only for classes
   * that implement {@link Comparable}.
   *
   * <p>Note: the type parameter {@code E} extends {@code Comparable<?>} rather than {@code
   * Comparable<? super E>} as a workaround for javac <a
   * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6468354">bug 6468354</a>.
   */
  public static <E extends Comparable<?>> Builder<E> naturalOrder() {
    return new Builder<E>(Ordering.natural());
  }

  /**
   * A builder for creating immutable multiset instances, especially {@code public static final}
   * multisets ("constant multisets"). Example:
   *
   * <pre>{@code
   * public static final ImmutableSortedMultiset<Bean> BEANS =
   *     new ImmutableSortedMultiset.Builder<Bean>(colorComparator())
   *         .addCopies(Bean.COCOA, 4)
   *         .addCopies(Bean.GARDEN, 6)
   *         .addCopies(Bean.RED, 8)
   *         .addCopies(Bean.BLACK_EYED, 10)
   *         .build();
   * }</pre>
   *
   * <p>Builder instances can be reused; it is safe to call {@link #build} multiple times to build
   * multiple multisets in series.
   *
   * 
   */
  public static class Builder<E> extends ImmutableMultiset.Builder<E> {
    /**
     * Creates a new builder. The returned builder is equivalent to the builder generated by {@link
     * ImmutableSortedMultiset#orderedBy(Comparator)}.
     */
    public Builder(Comparator<? super E> comparator) {
      super(TreeMultiset.create(checkNotNull(comparator)));
    }

    /**
     * Adds {@code element} to the {@code ImmutableSortedMultiset}.
     *
     * @param element the element to add
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code element} is null
     */

    @Override
    public Builder<E> add(E element) {
      super.add(element);
      return this;
    }

    /**
     * Adds each element of {@code elements} to the {@code ImmutableSortedMultiset}.
     *
     * @param elements the elements to add
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code elements} is null or contains a null element
     */

    @Override
    public Builder<E> add(E... elements) {
      super.add(elements);
      return this;
    }

    /**
     * Adds a number of occurrences of an element to this {@code ImmutableSortedMultiset}.
     *
     * @param element the element to add
     * @param occurrences the number of occurrences of the element to add. May be zero, in which
     *     case no change will be made.
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code element} is null
     * @throws IllegalArgumentException if {@code occurrences} is negative, or if this operation
     *     would result in more than {@link Integer#MAX_VALUE} occurrences of the element
     */

    @Override
    public Builder<E> addCopies(E element, int occurrences) {
      super.addCopies(element, occurrences);
      return this;
    }

    /**
     * Adds or removes the necessary occurrences of an element such that the element attains the
     * desired count.
     *
     * @param element the element to add or remove occurrences of
     * @param count the desired count of the element in this multiset
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code element} is null
     * @throws IllegalArgumentException if {@code count} is negative
     */

    @Override
    public Builder<E> setCount(E element, int count) {
      super.setCount(element, count);
      return this;
    }

    /**
     * Adds each element of {@code elements} to the {@code ImmutableSortedMultiset}.
     *
     * @param elements the {@code Iterable} to add to the {@code ImmutableSortedMultiset}
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code elements} is null or contains a null element
     */

    @Override
    public Builder<E> addAll(Iterable<? extends E> elements) {
      super.addAll(elements);
      return this;
    }

    /**
     * Adds each element of {@code elements} to the {@code ImmutableSortedMultiset}.
     *
     * @param elements the elements to add to the {@code ImmutableSortedMultiset}
     * @return this {@code Builder} object
     * @throws NullPointerException if {@code elements} is null or contains a null element
     */

    @Override
    public Builder<E> addAll(Iterator<? extends E> elements) {
      super.addAll(elements);
      return this;
    }

    /**
     * Returns a newly-created {@code ImmutableSortedMultiset} based on the contents of the {@code
     * Builder}.
     */
    @Override
    public ImmutableSortedMultiset<E> build() {
      return copyOfSorted((SortedMultiset<E>) contents);
    }
  }

  private static final class SerializedForm<E> implements Serializable {
    final Comparator<? super E> comparator;
    final E[] elements;
    final int[] counts;

    @SuppressWarnings("unchecked")
    SerializedForm(SortedMultiset<E> multiset) {
      this.comparator = multiset.comparator();
      int n = multiset.entrySet().size();
      elements = (E[]) new Object[n];
      counts = new int[n];
      int i = 0;
      for (Entry<E> entry : multiset.entrySet()) {
        elements[i] = entry.getElement();
        counts[i] = entry.getCount();
        i++;
      }
    }

    Object readResolve() {
      int n = elements.length;
      Builder<E> builder = new Builder<>(comparator);
      for (int i = 0; i < n; i++) {
        builder.addCopies(elements[i], counts[i]);
      }
      return builder.build();
    }
  }

  @Override
  Object writeReplace() {
    return new SerializedForm<E>(this);
  }
}
