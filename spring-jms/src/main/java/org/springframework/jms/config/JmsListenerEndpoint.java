

package org.springframework.jms.config;

import org.springframework.jms.listener.MessageListenerContainer;

/**
 * Model for a JMS listener endpoint. Can be used against a
 * {@link org.springframework.jms.annotation.JmsListenerConfigurer
 * JmsListenerConfigurer} to register endpoints programmatically.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface JmsListenerEndpoint {

	/**
	 * Return the id of this endpoint.
	 */
	String getId();

	/**
	 * Setup the specified message listener container with the model
	 * defined by this endpoint.
	 * xmlBeanDefinitionReaderThis endpoint must provide the requested missing option(s) of
	 * the specified container to make it usable. Usually, this is about
	 * setting the {@code destination} and the {@code messageListener} to
	 * use but an implementation may override any default setting that
	 * was already set.
	 * @param listenerContainer the listener container to configure
	 */
	void setupListenerContainer(MessageListenerContainer listenerContainer);

}
