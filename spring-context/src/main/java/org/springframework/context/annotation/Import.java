

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates one or more {@link Configuration @Configuration} classes to import.
 *
 * Provides functionality equivalent to the {@code <import/>} element in Spring XML.
 * Allows for importing {@code @Configuration} classes, {@link ImportSelector} and {@link ImportBeanDefinitionRegistrar} implementations,
 * as well as regular component classes (as of 4.2; analogous to {@link AnnotationConfigApplicationContext#register}).
 * {@code @Bean} definitions declared in imported {@code @Configuration} classes should be
 * accessed by using {@link org.springframework.beans.factory.annotation.Autowired @Autowired} injection.
 * Either the bean itself can be autowired, or the configuration class instance declaring the bean can be autowired.
 * The latter approach allows for explicit, IDE-friendly  navigation between {@code @Configuration} class methods.
 * May be declared at the class level or as a meta-annotation.
 * If XML or other non-{@code @Configuration} bean definition resources need to be imported, use the {@link ImportResource @ImportResource} annotation instead.
 * @since 3.0
 * @see Configuration
 * @see ImportSelector
 * @see ImportResource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	/**
	 * {@link Configuration}, {@link ImportSelector}, {@link ImportBeanDefinitionRegistrar} or regular component classes to import.
	 */
	Class<?>[] value();

}
