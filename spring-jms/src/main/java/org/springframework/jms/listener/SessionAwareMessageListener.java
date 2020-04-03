

package org.springframework.jms.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Variant of the standard JMS {@link javax.jms.MessageListener} interface,
 * offering not only the received Message but also the underlying
 * JMS Session object. The latter can be used to send reply messages,
 * without the need to access an external Connection/Session,
 * i.e. without the need to access the underlying ConnectionFactory.
 *
 * xmlBeanDefinitionReaderSupported by Spring's {@link DefaultMessageListenerContainer}
 * and {@link SimpleMessageListenerContainer},
 * as direct alternative to the standard JMS MessageListener interface.
 * Typically <i>not</i> supported by JCA-based listener containers:
 * For maximum compatibility, implement a standard JMS MessageListener instead.
 *

 * @since 2.0
 * @param <M> the message type
 * @see AbstractMessageListenerContainer#setMessageListener
 * @see DefaultMessageListenerContainer
 * @see SimpleMessageListenerContainer
 * @see org.springframework.jms.listener.endpoint.JmsMessageEndpointManager
 * @see javax.jms.MessageListener
 */
@FunctionalInterface
public interface SessionAwareMessageListener<M extends Message> {

	/**
	 * Callback for processing a received JMS message.
	 * xmlBeanDefinitionReaderImplementors are supposed to process the given Message,
	 * typically sending reply messages through the given Session.
	 * @param message the received JMS message (never {@code null})
	 * @param session the underlying JMS Session (never {@code null})
	 * @throws JMSException if thrown by JMS methods
	 */
	void onMessage(M message, Session session) throws JMSException;

}
