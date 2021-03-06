

package org.springframework.web.reactive.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Adding this annotation to an {@code @Configuration} class imports the Spring
 * WebFlux configuration from {@link WebFluxConfigurationSupport} that enables
 * use of annotated controllers and functional endpoints.
 *
 * For example:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebFlux
 * &#064;ComponentScan(basePackageClasses = MyConfiguration.class)
 * public class MyConfiguration {
 * }
 * </pre>
 *
 * To customize the imported configuration, implement
 * {@link WebFluxConfigurer} and one or more of its methods:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebFlux
 * &#064;ComponentScan(basePackageClasses = MyConfiguration.class)
 * public class MyConfiguration implements WebFluxConfigurer {
 *
 * 	   &#064;Override
 * 	   public void configureMessageWriters(List&lt;HttpMessageWriter&lt;?&gt&gt messageWriters) {
 *         messageWriters.add(new MyHttpMessageWriter());
 * 	   }
 *
 * 	   // ...
 * }
 * </pre>
 *
 * Only one {@code @Configuration} class should have the {@code @EnableWebFlux}
 * annotation in order to import the Spring WebFlux configuration. There can
 * however be multiple {@code @Configuration} classes that implement
 * {@code WebFluxConfigurer} that customize the provided configuration.
 *
 * If {@code WebFluxConfigurer} does not expose some setting that needs to be
 * configured, consider switching to an advanced mode by removing the
 * {@code @EnableWebFlux} annotation and extending directly from
 * {@link WebFluxConfigurationSupport} or {@link DelegatingWebFluxConfiguration} --
 * the latter allows detecting and delegating to one or more
 * {@code WebFluxConfigurer} configuration classes.
 *
 * @author Brian Clozel
 *
 * @since 5.0
 * @see WebFluxConfigurer
 * @see WebFluxConfigurationSupport
 * @see DelegatingWebFluxConfiguration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingWebFluxConfiguration.class)
public @interface EnableWebFlux {
}
