

package org.springframework.context.annotation;

import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * Interface to be implemented by
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * classes annotated with {@link EnableLoadTimeWeaving @EnableLoadTimeWeaving} that wish to
 * customize the {@link LoadTimeWeaver} instance to be used.
 *
 * See {@link org.springframework.scheduling.annotation.EnableAsync @EnableAsync}
 * for usage examples and information on how a default {@code LoadTimeWeaver}
 * is selected when this interface is not used.

 * @since 3.1
 * @see LoadTimeWeavingConfiguration
 * @see EnableLoadTimeWeaving
 */
public interface LoadTimeWeavingConfigurer {

	/**
	 * Create, configure and return the {@code LoadTimeWeaver} instance to be used. Note
	 * that it is unnecessary to annotate this method with {@code @Bean}, because the
	 * object returned will automatically be registered as a bean by
	 * {@link LoadTimeWeavingConfiguration#loadTimeWeaver()}
	 */
	LoadTimeWeaver getLoadTimeWeaver();

}
