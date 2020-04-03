

package org.springframework.jms.support.destination;

/**
 * Extension of the DestinationResolver interface,
 * exposing methods for clearing the cache.
 *

 * @since 2.0
 */
public interface CachingDestinationResolver extends DestinationResolver {

	/**
	 * Remove the destination with the given name from the cache
	 * (if cached by this resolver in the first place).
	 * xmlBeanDefinitionReaderTo be called if access to the specified destination failed,
	 * assuming that the JMS Destination object might have become invalid.
	 * @param destinationName the name of the destination
	 */
	void removeFromCache(String destinationName);

	/**
	 * Clear the entire destination cache.
	 * xmlBeanDefinitionReaderTo be called in case of general JMS provider failure.
	 */
	void clearCache();

}
