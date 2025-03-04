

package com.whaleal.icefrog.core.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The class to which this annotation is applied is not thread-safe. This annotation primarily exists for clarifying the non-thread-safety
 * of a class that might otherwise be assumed to be thread-safe, despite the fact that it is a bad idea to assume a class is thread-safe
 * without good reason.
 *
 * @see ThreadSafe
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotThreadSafe {
}
