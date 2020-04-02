

package org.springframework.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;

/**
 * A common Spring annotation to declare that annotated elements can be {@code null} under some circumstance.
 * Leverages JSR-305 meta-annotations to indicate nullability in Java to common tools with JSR-305 support and used by Kotlin to infer nullability of Spring API.
 * Should be used at parameter, return value, and field level. Methods override should repeat parent {@code @Nullable} annotations unless they behave differently.
 * Can be used in association with {@code @NonNullApi} or {@code @NonNullFields} to override the default non-nullable semantic to nullable.
 * @since 5.0
 * @see NonNullApi
 * @see NonNullFields
 * @see NonNull
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull(when = When.MAYBE)
@TypeQualifierNickname
public @interface Nullable {
}
