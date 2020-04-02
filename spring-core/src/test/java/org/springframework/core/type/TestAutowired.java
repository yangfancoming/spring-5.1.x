

package org.springframework.core.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
public @interface TestAutowired {

	/**
	 * Declares whether the annotated dependency is required.
	 * Defaults to {@code true}.
	 */
	boolean required() default true;

}
