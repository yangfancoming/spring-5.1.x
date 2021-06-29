

package org.springframework.jms.core;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * To be used with JmsTemplate's send method that convert an object to a message.
 * It allows for further modification of the message after it has been processed
 * by the converter. This is useful for setting of JMS Header and Properties.
 *
 * xmlBeanDefinitionReaderThis often as an anonymous class within a method implementation.
 *
 * @author Mark Pollack
 * @since 1.1
 * @see JmsTemplate#convertAndSend(String, Object, MessagePostProcessor)
 * @see JmsTemplate#convertAndSend(javax.jms.Destination, Object, MessagePostProcessor)
 * @see org.springframework.jms.support.converter.MessageConverter
 */
public interface MessagePostProcessor {

	/**
	 * Apply a MessagePostProcessor to the message. The returned message is
	 * typically a modified version of the original.
	 * @param message the JMS message from the MessageConverter
	 * @return the modified version of the Message
	 * @throws javax.jms.JMSException if thrown by JMS API methods
	 */
	Message postProcessMessage(Message message) throws JMSException;

}
