

package org.springframework.jms.support.converter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Strategy interface that specifies a converter between Java objects and JMS messages.
 *
 * xmlBeanDefinitionReaderCheck out {@link SimpleMessageConverter} for a default implementation,
 * converting between the 'standard' message payloads and JMS Message types.
 *
 * @author Mark Pollack

 * @since 1.1
 * @see org.springframework.jms.core.JmsTemplate#setMessageConverter
 * @see org.springframework.jms.listener.adapter.MessageListenerAdapter#setMessageConverter
 * @see org.springframework.jms.remoting.JmsInvokerClientInterceptor#setMessageConverter
 * @see org.springframework.jms.remoting.JmsInvokerServiceExporter#setMessageConverter
 */
public interface MessageConverter {

	/**
	 * Convert a Java object to a JMS Message using the supplied session
	 * to create the message object.
	 * @param object the object to convert
	 * @param session the Session to use for creating a JMS Message
	 * @return the JMS Message
	 * @throws javax.jms.JMSException if thrown by JMS API methods
	 * @throws MessageConversionException in case of conversion failure
	 */
	Message toMessage(Object object, Session session) throws JMSException, MessageConversionException;

	/**
	 * Convert from a JMS Message to a Java object.
	 * @param message the message to convert
	 * @return the converted Java object
	 * @throws javax.jms.JMSException if thrown by JMS API methods
	 * @throws MessageConversionException in case of conversion failure
	 */
	Object fromMessage(Message message) throws JMSException, MessageConversionException;

}
