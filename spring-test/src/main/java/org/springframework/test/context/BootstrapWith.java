

package org.springframework.test.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @BootstrapWith} defines class-level metadata that is used to determine
 * how to bootstrap the <em>Spring TestContext Framework</em>.
 *
 * This annotation may also be used as a <em>meta-annotation</em> to create
 * custom <em>composed annotations</em>. As of Spring Framework 5.1, a locally
 * declared {@code @BootstrapWith} annotation (i.e., one that is <em>directly
 * present</em> on the current test class) will override any meta-present
 * declarations of {@code @BootstrapWith}.
 *
 * @author Sam Brannen
 * @since 4.1
 * @see BootstrapContext
 * @see TestContextBootstrapper
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BootstrapWith {

	/**
	 * The {@link TestContextBootstrapper} to use to bootstrap the <em>Spring
	 * TestContext Framework</em>.
	 */
	Class<? extends TestContextBootstrapper> value() default TestContextBootstrapper.class;

}
