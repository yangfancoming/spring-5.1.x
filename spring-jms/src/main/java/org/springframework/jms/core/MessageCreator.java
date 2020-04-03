

package org.springframework.jms.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Creates a JMS message given a {@link Session}.
 *
 * xmlBeanDefinitionReaderThe {@code Session} typically is provided by an instance
 * of the {@link JmsTemplate} class.
 *
 * xmlBeanDefinitionReaderImplementations <i>do not</i> need to concern themselves with
 * checked {@code JMSExceptions} (from the '{@code javax.jms}'
 * package) that may be thrown from operations they attempt. The
 * {@code JmsTemplate} will catch and handle these
 * {@code JMSExceptions} appropriately.
 *
 * @author Mark Pollack
 * @since 1.1
 */
@FunctionalInterface
public interface MessageCreator {

	/**
	 * Create a {@link Message} to be sent.
	 * @param session the JMS {@link Session} to be used to create the
	 * {@code Message} (never {@code null})
	 * @return the {@code Message} to be sent
	 * @throws javax.jms.JMSException if thrown by JMS API methods
	 */
	Message createMessage(Session session) throws JMSException;

}
