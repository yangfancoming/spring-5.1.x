

package org.springframework.jms.support.destination;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.lang.Nullable;

/**
 * Strategy interface for resolving JMS destinations.
 *
 * xmlBeanDefinitionReaderUsed by {@link org.springframework.jms.core.JmsTemplate} for resolving
 * destination names from simple {@link String Strings} to actual
 * {@link Destination} implementation instances.
 *
 * xmlBeanDefinitionReaderThe default {@link DestinationResolver} implementation used by
 * {@link org.springframework.jms.core.JmsTemplate} instances is the
 * {@link DynamicDestinationResolver} class. Consider using the
 * {@link JndiDestinationResolver} for more advanced scenarios.
 *

 * @since 1.1
 * @see org.springframework.jms.core.JmsTemplate#setDestinationResolver
 * @see org.springframework.jms.support.destination.DynamicDestinationResolver
 * @see org.springframework.jms.support.destination.JndiDestinationResolver
 */
@FunctionalInterface
public interface DestinationResolver {

	/**
	 * Resolve the given destination name, either as located resource
	 * or as dynamic destination.
	 * @param session the current JMS Session
	 * (may be {@code null} if the resolver implementation is able to work without it)
	 * @param destinationName the name of the destination
	 * @param pubSubDomain {@code true} if the domain is pub-sub, {@code false} if P2P
	 * @return the JMS destination (either a topic or a queue)
	 * @throws javax.jms.JMSException if the JMS Session failed to resolve the destination
	 * @throws DestinationResolutionException in case of general destination resolution failure
	 */
	Destination resolveDestinationName(@Nullable Session session, String destinationName, boolean pubSubDomain)
			throws JMSException;

}
