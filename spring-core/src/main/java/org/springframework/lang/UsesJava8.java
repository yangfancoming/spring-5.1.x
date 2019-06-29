

package org.springframework.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element uses Java 8 specific API constructs,
 * without implying that it strictly requires Java 8.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @deprecated as of 5.0 since the framework is based on Java 8+ now
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Documented
@Deprecated
public @interface UsesJava8 {
}
