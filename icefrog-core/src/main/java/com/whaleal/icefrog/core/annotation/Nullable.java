
package com.whaleal.icefrog.core.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A common annotation to declare that annotated elements can be {@code null} under some circumstance. Leverages JSR 305 meta-annotations to
 * indicate nullability in Java to common tools with JSR 305 support and used by Kotlin to infer nullability of Spring API.
 *
 * <p>Should be used at parameter, return value, and field level. Methods override should repeat parent {@code @Nullable} annotations
 * unless they behave differently.</p>
 *
 * <p>Can be used in association with {@code NonNullApi} to override the default non-nullable semantic to nullable.</p>
 *
 * @see NonNullApi
 * @see NonNull
 *
 * @author wh
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull(when = When.MAYBE)
@TypeQualifierNickname
public @interface Nullable {
}
