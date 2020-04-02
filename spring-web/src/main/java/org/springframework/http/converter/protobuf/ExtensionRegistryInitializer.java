

package org.springframework.http.converter.protobuf;

import com.google.protobuf.ExtensionRegistry;

/**
 * Google Protocol Messages can contain message extensions that can be parsed if
 * the appropriate configuration has been registered in the {@code ExtensionRegistry}.
 *
 * This interface provides a facility to populate the {@code ExtensionRegistry}.
 *
 * @author Alex Antonov
 * @author Sebastien Deleuze
 * @since 4.1
 * @see <a href="https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/ExtensionRegistry">
 * com.google.protobuf.ExtensionRegistry</a>
 * @deprecated as of Spring Framework 5.1, use {@link ExtensionRegistry} based constructors instead
 */
@Deprecated
public interface ExtensionRegistryInitializer {

	/**
	 * Initializes the {@code ExtensionRegistry} with Protocol Message extensions.
	 * @param registry the registry to populate
	 */
	void initializeExtensionRegistry(ExtensionRegistry registry);

}
