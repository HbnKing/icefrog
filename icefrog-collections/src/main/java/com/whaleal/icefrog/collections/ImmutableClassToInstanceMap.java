

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.ClassUtil;
import com.whaleal.icefrog.core.util.NumberUtil;


import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Map;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;

/**
 * A {@link ClassToInstanceMap} whose contents will never change, with many other important
 * properties detailed at {@link ImmutableCollection}.
 *
 * @author Kevin Bourrillion
 * 
 */



public final class ImmutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B>
    implements ClassToInstanceMap<B>, Serializable {

  private static final ImmutableClassToInstanceMap<Object> EMPTY =
      new ImmutableClassToInstanceMap<>(ImmutableMap.of());

  /**
   * Returns an empty {@code ImmutableClassToInstanceMap}.
   *
   * <p><b>Performance note:</b> the instance returned is a singleton.
   *
   * 
   */
  @SuppressWarnings("unchecked")
  public static <B> ImmutableClassToInstanceMap<B> of() {
    return (ImmutableClassToInstanceMap<B>) EMPTY;
  }

  /**
   * Returns an {@code ImmutableClassToInstanceMap} containing a single entry.
   *
   * 
   */
  public static <B, T extends B> ImmutableClassToInstanceMap<B> of(Class<T> type, T value) {
    ImmutableMap<Class<? extends B>, B> map = ImmutableMap.of(type, value);
    return new ImmutableClassToInstanceMap<>(map);
  }

  /**
   * Returns a new builder. The generated builder is equivalent to the builder created by the {@link
   * Builder} constructor.
   */
  public static <B> Builder<B> builder() {
    return new Builder<>();
  }

  /**
   * A builder for creating immutable class-to-instance MapUtil. Example:
   *
   * <pre>{@code
   * static final ImmutableClassToInstanceMap<Handler> HANDLERS =
   *     new ImmutableClassToInstanceMap.Builder<Handler>()
   *         .put(FooHandler.class, new FooHandler())
   *         .put(BarHandler.class, new SubBarHandler())
   *         .put(Handler.class, new QuuxHandler())
   *         .build();
   * }</pre>
   *
   * <p>After invoking {@link #build()} it is still possible to add more entries and build again.
   * Thus each map generated by this builder will be a superset of any map generated before it.
   *
   * 
   */
  public static final class Builder<B> {
    private final ImmutableMap.Builder<Class<? extends B>, B> mapBuilder = ImmutableMap.builder();

    /**
     * Associates {@code key} with {@code value} in the built map. Duplicate keys are not allowed,
     * and will cause {@link #build} to fail.
     */

    public <T extends B> Builder<B> put(Class<T> key, T value) {
      mapBuilder.put(key, value);
      return this;
    }

    /**
     * Associates all of {@code map's} keys and values in the built map. Duplicate keys are not
     * allowed, and will cause {@link #build} to fail.
     *
     * @throws NullPointerException if any key or value in {@code map} is null
     * @throws ClassCastException if any value is not an instance of the type specified by its key
     */

    public <T extends B> Builder<B> putAll(Map<? extends Class<? extends T>, ? extends T> map) {
      for (Entry<? extends Class<? extends T>, ? extends T> entry : map.entrySet()) {
        Class<? extends T> type = entry.getKey();
        T value = entry.getValue();
        mapBuilder.put(type, cast(type, value));
      }
      return this;
    }

    private static <B, T extends B> T cast(Class<T> type, B value) {

      return ClassUtil.getWrapperTypeIfPrimitive(type).cast(value);

    }

    /**
     * Returns a new immutable class-to-instance map containing the entries provided to this
     * builder.
     *
     * @throws IllegalArgumentException if duplicate keys were added
     */
    public ImmutableClassToInstanceMap<B> build() {
      ImmutableMap<Class<? extends B>, B> map = mapBuilder.build();
      if (map.isEmpty()) {
        return of();
      } else {
        return new ImmutableClassToInstanceMap<>(map);
      }
    }
  }

  /**
   * Returns an immutable map containing the same entries as {@code map}. If {@code map} somehow
   * contains entries with duplicate keys (for example, if it is a {@code SortedMap} whose
   * comparator is not <i>consistent with equals</i>), the results of this method are undefined.
   *
   * <p><b>Note:</b> Despite what the method name suggests, if {@code map} is an {@code
   * ImmutableClassToInstanceMap}, no copy will actually be performed.
   *
   * @throws NullPointerException if any key or value in {@code map} is null
   * @throws ClassCastException if any value is not an instance of the type specified by its key
   */
  public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(
      Map<? extends Class<? extends S>, ? extends S> map) {
    if (map instanceof ImmutableClassToInstanceMap) {
      @SuppressWarnings("unchecked") // covariant casts safe (unmodifiable)
      ImmutableClassToInstanceMap<B> cast = (ImmutableClassToInstanceMap<B>) map;
      return cast;
    }
    return new Builder<B>().putAll(map).build();
  }

  private final ImmutableMap<Class<? extends B>, B> delegate;

  private ImmutableClassToInstanceMap(ImmutableMap<Class<? extends B>, B> delegate) {
    this.delegate = delegate;
  }

  @Override
  protected Map<Class<? extends B>, B> delegate() {
    return delegate;
  }

  @Override
  @SuppressWarnings("unchecked") // value could not get in if not a T
  @CheckForNull
  public <T extends B> T getInstance(Class<T> type) {
    return (T) delegate.get(checkNotNull(type));
  }

  /**
   * Guaranteed to throw an exception and leave the map unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */

  @Deprecated
  @Override

  @CheckForNull
  public <T extends B> T putInstance(Class<T> type, T value) {
    throw new UnsupportedOperationException();
  }

  Object readResolve() {
    return isEmpty() ? of() : this;
  }
}
