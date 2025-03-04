package com.whaleal.icefrog.core.io;


import com.whaleal.icefrog.core.exceptions.Throwables;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;


/**
 * A {@link Closeable} that collects {@code Closeable} resources and closes them all when it is
 * {@linkplain #close closed}. This is intended to approximately emulate the behavior of Java 7's <a
 * href="http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html"
 * >try-with-resources</a> statement in JDK6-compatible code. Running on Java 7, code using this
 * should be approximately equivalent in behavior to the same code written with try-with-resources.
 * Running on Java 6, exceptions that cannot be thrown must be logged rather than being added to the
 * thrown exception as a suppressed exception.
 *
 * <p>This class is intended to be used in the following pattern:
 *
 * <pre>{@code
 * Closer closer = Closer.create();
 * try {
 *   InputStream in = closer.register(openInputStream());
 *   OutputStream out = closer.register(openOutputStream());
 *   // do stuff
 * } catch (Throwable e) {
 *   // ensure that any checked exception types other than IOException that could be thrown are
 *   // provided here, e.g. throw closer.rethrow(e, CheckedException.class);
 *   throw closer.rethrow(e);
 * } finally {
 *   closer.close();
 * }
 * }</pre>
 *
 * <p>Note that this try-catch-finally block is not equivalent to a try-catch-finally block using
 * try-with-resources. To get the equivalent of that, you must wrap the above code in <i>another</i>
 * try block in order to catch any exception that may be thrown (including from the call to {@code
 * close()}).
 *
 * <p>This pattern ensures the following:
 *
 * <ul>
 *   <li>Each {@code Closeable} resource that is successfully registered will be closed later.
 *   <li>If a {@code Throwable} is thrown in the try block, no exceptions that occur when attempting
 *       to close resources will be thrown from the finally block. The throwable from the try block
 *       will be thrown.
 *   <li>If no exceptions or errors were thrown in the try block, the <i>first</i> exception thrown
 *       by an attempt to close a resource will be thrown.
 *   <li>Any exception caught when attempting to close a resource that is <i>not</i> thrown (because
 *       another exception is already being thrown) is <i>suppressed</i>.
 * </ul>
 *
 * <p>An exception that is suppressed is not thrown. The method of suppression used depends on the
 * version of Java the code is running on:
 *
 * <ul>
 *   <li><b>Java 7+:</b> Exceptions are suppressed by adding them to the exception that <i>will</i>
 *       be thrown using {@code Throwable.addSuppressed(Throwable)}.
 *   <li><b>Java 6:</b> Exceptions are suppressed by logging them instead.
 * </ul>
 *
 * @author Colin Decker
 * @author wh
 */
// Coffee's for {@link Closer closers} only.



public final class Closer implements Closeable {

	/**
	 * The suppressor implementation to use for the current Java version.
	 */
	private static final Suppressor SUPPRESSOR;

	static {
		SuppressingSuppressor suppressingSuppressor = SuppressingSuppressor.tryCreate();
		SUPPRESSOR = suppressingSuppressor == null ? LoggingSuppressor.INSTANCE : suppressingSuppressor;
	}

	final Suppressor suppressor;
	// only need space for 2 elements in most cases, so try to use the smallest array possible
	private final Deque<Closeable> stack = new ArrayDeque<>(4);
	private Throwable thrown;
	Closer(Suppressor suppressor) {
		this.suppressor = checkNotNull(suppressor); // checkNotNull to satisfy null tests
	}

	/**
	 * Creates a new {@link Closer}.
	 * @return  创建一个新的Closer
	 */
	public static Closer create() {
		return new Closer(SUPPRESSOR);
	}

	/**
	 * Registers the given {@code closeable} to be closed when this {@code Closer} is {@linkplain
	 * #close closed}.
	 *
	 * @param closeable  closeable
	 * @param <C>  closeable
	 * @return the given {@code closeable}
	 */
	// close. this word no longer has any meaning to me.
	public <C extends Closeable> C register(C closeable) {
		if (closeable != null) {
			stack.addFirst(closeable);
		}

		return closeable;
	}

	/**
	 * Stores the given throwable and rethrows it. It will be rethrown as is if it is an {@code
	 * IOException}, {@code RuntimeException} or {@code Error}. Otherwise, it will be rethrown wrapped
	 * in a {@code RuntimeException}. <b>Note:</b> Be sure to declare all of the checked exception
	 * types your try block can throw when calling an overload of this method so as to avoid losing
	 * the original exception type.
	 *
	 * <p>This method always throws, and as such should be called as {@code throw closer.rethrow(e);}
	 * to ensure the compiler knows that it will throw.
	 * @param e  throwable
	 * @return this method does not return; it always throws
	 * @throws IOException when the given throwable is an IOException
	 */
	public RuntimeException rethrow(Throwable e) throws IOException {
		checkNotNull(e);
		thrown = e;
		Throwables.propagateIfPossible(e, IOException.class);
		throw new RuntimeException(e);
	}

	/**
	 * Stores the given throwable and rethrows it. It will be rethrown as is if it is an {@code
	 * IOException}, {@code RuntimeException}, {@code Error} or a checked exception of the given type.
	 * Otherwise, it will be rethrown wrapped in a {@code RuntimeException}. <b>Note:</b> Be sure to
	 * declare all of the checked exception types your try block can throw when calling an overload of
	 * this method so as to avoid losing the original exception type.
	 *
	 * <p>This method always throws, and as such should be called as {@code throw closer.rethrow(e,
	 * ...);} to ensure the compiler knows that it will throw.
	 *
	 * @param declaredType type
	 * @param <X>  x
	 * @param e  e
	 * @return this method does not return; it always throws
	 * @throws IOException when the given throwable is an IOException
	 * @throws X           when the given throwable is of the declared type X
	 */
	public <X extends Exception> RuntimeException rethrow(Throwable e, Class<X> declaredType)
			throws IOException, X {
		checkNotNull(e);
		thrown = e;
		Throwables.propagateIfPossible(e, IOException.class);
		Throwables.propagateIfPossible(e, declaredType);
		throw new RuntimeException(e);
	}

	/**
	 * Stores the given throwable and rethrows it. It will be rethrown as is if it is an {@code
	 * IOException}, {@code RuntimeException}, {@code Error} or a checked exception of either of the
	 * given types. Otherwise, it will be rethrown wrapped in a {@code RuntimeException}. <b>Note:</b>
	 * Be sure to declare all of the checked exception types your try block can throw when calling an
	 * overload of this method so as to avoid losing the original exception type.
	 *
	 * <p>This method always throws, and as such should be called as {@code throw closer.rethrow(e,
	 * ...);} to ensure the compiler knows that it will throw.
	 *
	 *
	 * @param declaredType1  type1
	 * @param declaredType2 type2
	 * @param e e
	 * @param <X1> x
	 * @param <X2> x
	 * @return this method does not return; it always throws
	 * @throws IOException when the given throwable is an IOException
	 * @throws X1          when the given throwable is of the declared type X1
	 * @throws X2          when the given throwable is of the declared type X2
	 */
	public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(
			Throwable e, Class<X1> declaredType1, Class<X2> declaredType2) throws IOException, X1, X2 {
		checkNotNull(e);
		thrown = e;
		Throwables.propagateIfPossible(e, IOException.class);
		Throwables.propagateIfPossible(e, declaredType1, declaredType2);
		throw new RuntimeException(e);
	}

	/**
	 * Closes all {@code Closeable} instances that have been added to this {@code Closer}. If an
	 * exception was thrown in the try block and passed to one of the {@code exceptionThrown} methods,
	 * any exceptions thrown when attempting to close a closeable will be suppressed. Otherwise, the
	 * <i>first</i> exception to be thrown from an attempt to close a closeable will be thrown and any
	 * additional exceptions that are thrown after that will be suppressed.
	 *
	 * @throws IOException throw IOException
	 */
	@Override
	public void close() throws IOException {
		Throwable throwable = thrown;

		// close closeables in LIFO order
		while (!stack.isEmpty()) {
			Closeable closeable = stack.removeFirst();
			try {
				closeable.close();
			} catch (Throwable e) {
				if (throwable == null) {
					throwable = e;
				} else {
					suppressor.suppress(closeable, throwable, e);
				}
			}
		}

		if (thrown == null && throwable != null) {
			Throwables.propagateIfPossible(throwable, IOException.class);
			throw new AssertionError(throwable); // not possible
		}
	}

	/**
	 * Suppression strategy interface.
	 */

	interface Suppressor {
		/**
		 * Suppresses the given exception ({@code suppressed}) which was thrown when attempting to close
		 * the given closeable. {@code thrown} is the exception that is actually being thrown from the
		 * method. Implementations of this method should not throw under any circumstances.
		 */
		void suppress(Closeable closeable, Throwable thrown, Throwable suppressed);
	}

	/**
	 * Suppresses exceptions by logging them.
	 */

	static final class LoggingSuppressor implements Suppressor {

		static final LoggingSuppressor INSTANCE = new LoggingSuppressor();

		@Override
		public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
			// log to the same place as Closeables
			Closeables.logger.log(
					Level.WARNING, "Suppressing exception thrown when closing " + closeable, suppressed);
		}
	}

	/**
	 * Suppresses exceptions by adding them to the exception that will be thrown using JDK7's
	 * addSuppressed(Throwable) mechanism.
	 */

	static final class SuppressingSuppressor implements Suppressor {

		private final Method addSuppressed;

		private SuppressingSuppressor(Method addSuppressed) {
			this.addSuppressed = addSuppressed;
		}

		static SuppressingSuppressor tryCreate() {
			Method addSuppressed;
			try {
				addSuppressed = Throwable.class.getMethod("addSuppressed", Throwable.class);
			} catch (Throwable e) {
				return null;
			}
			return new SuppressingSuppressor(addSuppressed);
		}

		@Override
		public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
			// ensure no exceptions from addSuppressed
			if (thrown == suppressed) {
				return;
			}
			try {
				addSuppressed.invoke(thrown, suppressed);
			} catch (Throwable e) {
				// if, somehow, IllegalAccessException or another exception is thrown, fall back to logging
				LoggingSuppressor.INSTANCE.suppress(closeable, thrown, suppressed);
			}
		}
	}
}
