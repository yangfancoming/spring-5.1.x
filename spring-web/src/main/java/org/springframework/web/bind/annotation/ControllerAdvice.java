

package org.springframework.web.bind.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Specialization of {@link Component @Component} for classes that declare
 * {@link ExceptionHandler @ExceptionHandler}, {@link InitBinder @InitBinder}, or
 * {@link ModelAttribute @ModelAttribute} methods to be shared across
 * multiple {@code @Controller} classes.
 *
 * Classes annotated with {@code @ControllerAdvice} can be declared explicitly
 * as Spring beans or auto-detected via classpath scanning. All such beans are
 * sorted based on {@link org.springframework.core.annotation.Order @Order}
 * semantics and applied in that order at runtime. For handling exceptions, an
 * {@code @ExceptionHandler} will be picked on the first advice with a matching
 * exception handler method. For model attributes and {@code InitBinder}
 * initialization, {@code @ModelAttribute} and {@code @InitBinder} methods will
 * also follow {@code @ControllerAdvice} order.
 *
 * Note: For {@code @ExceptionHandler} methods, a root exception match will be
 * preferred to just matching a cause of the current exception, among the handler
 * methods of a particular advice bean. However, a cause match on a higher-priority
 * advice will still be preferred over any match (whether root or cause level)
 * on a lower-priority advice bean. As a consequence, please declare your primary
 * root exception mappings on a prioritized advice bean with a corresponding order.
 *
 * By default the methods in an {@code @ControllerAdvice} apply globally to
 * all controllers. Use selectors such as {@link #annotations},
 * {@link #basePackageClasses}, and {@link #basePackages} (or its alias
 * {@link #value}) to define a more narrow subset of targeted controllers.
 * If multiple selectors are declared, {@code OR} logic is applied, meaning selected
 * controllers should match at least one selector. Note that selector checks
 * are performed at runtime, so adding many selectors may negatively impact
 * performance and add complexity.
 *
 *
 * @author Brian Clozel
 * @author Sam Brannen
 * @since 3.2
 * @see org.springframework.stereotype.Controller
 * @see RestControllerAdvice
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerAdvice {

	/**
	 * Alias for the {@link #basePackages} attribute.
	 * Allows for more concise annotation declarations e.g.:
	 * {@code @ControllerAdvice("org.my.pkg")} is equivalent to
	 * {@code @ControllerAdvice(basePackages="org.my.pkg")}.
	 * @since 4.0
	 * @see #basePackages()
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * Array of base packages.
	 * Controllers that belong to those base packages or sub-packages thereof
	 * will be included, e.g.: {@code @ControllerAdvice(basePackages="org.my.pkg")}
	 * or {@code @ControllerAdvice(basePackages={"org.my.pkg", "org.my.other.pkg"})}.
	 * {@link #value} is an alias for this attribute, simply allowing for
	 * more concise use of the annotation.
	 * Also consider using {@link #basePackageClasses()} as a type-safe
	 * alternative to String-based package names.
	 * @since 4.0
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #value()} for specifying the packages
	 * to select Controllers to be assisted by the {@code @ControllerAdvice}
	 * annotated class.
	 * Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 * @since 4.0
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * Array of classes.
	 * Controllers that are assignable to at least one of the given types
	 * will be assisted by the {@code @ControllerAdvice} annotated class.
	 * @since 4.0
	 */
	Class<?>[] assignableTypes() default {};

	/**
	 * Array of annotations.
	 * Controllers that are annotated with this/one of those annotation(s)
	 * will be assisted by the {@code @ControllerAdvice} annotated class.
	 * Consider creating a special annotation or use a predefined one,
	 * like {@link RestController @RestController}.
	 * @since 4.0
	 */
	Class<? extends Annotation>[] annotations() default {};

}
