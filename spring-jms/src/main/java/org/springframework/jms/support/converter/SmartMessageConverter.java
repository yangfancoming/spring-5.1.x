

package org.springframework.jms.support.converter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.lang.Nullable;

/**
 * An extended {@link MessageConverter} SPI with conversion hint support.
 *
 * xmlBeanDefinitionReaderIn case of a conversion hint being provided, the framework will call
 * the extended method if a converter implements this interface, instead
 * of calling the regular {@code toMessage} variant.
 *
 * @author Stephane Nicoll
 * @since 4.3
 */
public interface SmartMessageConverter extends MessageConverter {

	/**
	 * A variant of {@link #toMessage(Object, Session)} which takes an extra conversion
	 * context as an argument, allowing to take e.g. annotations on a payload parameter
	 * into account.
	 * @param object the object to convert
	 * @param session the Session to use for creating a JMS Message
	 * @param conversionHint an extra object passed to the {@link MessageConverter},
	 * e.g. the associated {@code MethodParameter} (may be {@code null}}
	 * @return the JMS Message
	 * @throws javax.jms.JMSException if thrown by JMS API methods
	 * @throws MessageConversionException in case of conversion failure
	 * @see #toMessage(Object, Session)
	 */
	Message toMessage(Object object, Session session, @Nullable Object conversionHint)
			throws JMSException, MessageConversionException;

}
