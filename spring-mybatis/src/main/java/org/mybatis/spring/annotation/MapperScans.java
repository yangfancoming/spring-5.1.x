
package org.mybatis.spring.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * The Container annotation that aggregates several {@link MapperScan} annotations.
 * Can be used natively, declaring several nested {@link MapperScan} annotations. Can also be used in conjunction with
 * Java 8's support for repeatable annotations, where {@link MapperScan} can simply be declared several times on the
 * same method, implicitly generating this container annotation.
 * @since 2.0.0
 * @see MapperScan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MapperScannerRegistrar.RepeatingRegistrar.class)
public @interface MapperScans {
  MapperScan[] value();
}
