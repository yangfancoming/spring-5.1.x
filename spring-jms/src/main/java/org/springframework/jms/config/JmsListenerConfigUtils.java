

package org.springframework.jms.config;

/**
 * Configuration constants for internal sharing across subpackages.
 *

 * @since 4.1
 */
public abstract class JmsListenerConfigUtils {

	/**
	 * The bean name of the internally managed JMS listener annotation processor.
	 */
	public static final String JMS_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME =
			"org.springframework.jms.config.internalJmsListenerAnnotationProcessor";

	/**
	 * The bean name of the internally managed JMS listener endpoint registry.
	 */
	public static final String JMS_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME =
			"org.springframework.jms.config.internalJmsListenerEndpointRegistry";

}
