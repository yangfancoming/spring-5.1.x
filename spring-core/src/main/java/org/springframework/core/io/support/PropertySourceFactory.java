

package org.springframework.core.io.support;

import java.io.IOException;

import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

/**
 * Strategy interface for creating resource-based {@link PropertySource} wrappers.
 *

 * @since 4.3
 * @see DefaultPropertySourceFactory
 */
public interface PropertySourceFactory {

	/**
	 * Create a {@link PropertySource} that wraps the given resource.
	 * @param name the name of the property source
	 * @param resource the resource (potentially encoded) to wrap
	 * @return the new {@link PropertySource} (never {@code null})
	 * @throws IOException if resource resolution failed
	 */
	PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException;

}
