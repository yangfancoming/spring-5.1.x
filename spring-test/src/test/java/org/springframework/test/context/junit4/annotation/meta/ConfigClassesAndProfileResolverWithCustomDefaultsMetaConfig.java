

package org.springframework.test.context.junit4.annotation.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.ContextConfiguration;

/**
 * Custom configuration annotation with meta-annotation attribute overrides for
 * {@link ContextConfiguration#classes} and {@link ActiveProfiles#resolver} and
 * with default configuration local to the composed annotation.
 *
 * @author Sam Brannen
 * @since 4.0.3
 */
@ContextConfiguration
@ActiveProfiles
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigClassesAndProfileResolverWithCustomDefaultsMetaConfig {

	@Configuration
	@Profile("dev")
	static class DevConfig {

		@Bean
		public String foo() {
			return "Dev Foo";
		}
	}

	@Configuration
	@Profile("prod")
	static class ProductionConfig {

		@Bean
		public String foo() {
			return "Production Foo";
		}
	}

	@Configuration
	@Profile("resolver")
	static class ResolverConfig {

		@Bean
		public String foo() {
			return "Resolver Foo";
		}
	}

	static class CustomResolver implements ActiveProfilesResolver {

		@Override
		public String[] resolve(Class<?> testClass) {
			return testClass.getSimpleName().equals("ConfigClassesAndProfileResolverWithCustomDefaultsMetaConfigTests") ? new String[] { "resolver" }
					: new String[] {};
		}
	}


	Class<?>[] classes() default { DevConfig.class, ProductionConfig.class, ResolverConfig.class };

	Class<? extends ActiveProfilesResolver> resolver() default CustomResolver.class;

}
