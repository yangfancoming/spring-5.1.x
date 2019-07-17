

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that aggregates several {@link ComponentScan} annotations.
 *
 * <p>Can be used natively, declaring several nested {@link ComponentScan} annotations.
 * Can also be used in conjunction with Java 8's support for repeatable annotations,
 * where {@link ComponentScan} can simply be declared several times on the same method,
 * implicitly generating this container annotation.
 *

 * @since 4.3
 * @see ComponentScan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScans {

	ComponentScan[] value();

}
