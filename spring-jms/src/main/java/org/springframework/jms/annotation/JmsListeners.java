

package org.springframework.jms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that aggregates several {@link JmsListener} annotations.
 *
 * xmlBeanDefinitionReaderCan be used natively, declaring several nested {@link JmsListener} annotations.
 * Can also be used in conjunction with Java 8's support for repeatable annotations,
 * where {@link JmsListener} can simply be declared several times on the same method,
 * implicitly generating this container annotation.
 *
 * xmlBeanDefinitionReaderThis annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em>.
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see JmsListener
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JmsListeners {

	JmsListener[] value();

}
