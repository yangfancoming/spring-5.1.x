

package org.springframework.jms.listener.adapter;

import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.core.MethodParameter;
import org.springframework.jms.support.JmsHeaderMapper;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * A {@link javax.jms.MessageListener} adapter that invokes a configurable
 * {@link InvocableHandlerMethod}.
 *
 * xmlBeanDefinitionReaderWraps the incoming {@link javax.jms.Message} to Spring's {@link Message}
 * abstraction, copying the JMS standard headers using a configurable
 * {@link JmsHeaderMapper}.
 *
 * xmlBeanDefinitionReaderThe original {@link javax.jms.Message} and the {@link javax.jms.Session}
 * are provided as additional arguments so that these can be injected as
 * method arguments if necessary.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see Message
 * @see JmsHeaderMapper
 * @see InvocableHandlerMethod
 */
public class MessagingMessageListenerAdapter extends AbstractAdaptableMessageListener {

	@Nullable
	private InvocableHandlerMethod handlerMethod;


	/**
	 * Set the {@link InvocableHandlerMethod} to use to invoke the method
	 * processing an incoming {@link javax.jms.Message}.
	 */
	public void setHandlerMethod(InvocableHandlerMethod handlerMethod) {
		this.handlerMethod = handlerMethod;
	}

	private InvocableHandlerMethod getHandlerMethod() {
		Assert.state(this.handlerMethod != null, "No HandlerMethod set");
		return this.handlerMethod;
	}


	@Override
	public void onMessage(javax.jms.Message jmsMessage, @Nullable Session session) throws JMSException {
		Message<?> message = toMessagingMessage(jmsMessage);
		if (logger.isDebugEnabled()) {
			logger.debug("Processing [" + message + "]");
		}
		Object result = invokeHandler(jmsMessage, session, message);
		if (result != null) {
			handleResult(result, jmsMessage, session);
		}
		else {
			logger.trace("No result object given - no result to handle");
		}
	}

	@Override
	protected Object preProcessResponse(Object result) {
		MethodParameter returnType = getHandlerMethod().getReturnType();
		if (result instanceof Message) {
			return MessageBuilder.fromMessage((Message<?>) result)
					.setHeader(AbstractMessageSendingTemplate.CONVERSION_HINT_HEADER, returnType).build();
		}
		return MessageBuilder.withPayload(result).setHeader(
				AbstractMessageSendingTemplate.CONVERSION_HINT_HEADER, returnType).build();
	}

	protected Message<?> toMessagingMessage(javax.jms.Message jmsMessage) {
		try {
			return (Message<?>) getMessagingMessageConverter().fromMessage(jmsMessage);
		}
		catch (JMSException ex) {
			throw new MessageConversionException("Could not convert JMS message", ex);
		}
	}

	/**
	 * Invoke the handler, wrapping any exception to a {@link ListenerExecutionFailedException}
	 * with a dedicated error message.
	 */
	@Nullable
	private Object invokeHandler(javax.jms.Message jmsMessage, @Nullable Session session, Message<?> message) {
		InvocableHandlerMethod handlerMethod = getHandlerMethod();
		try {
			return handlerMethod.invoke(message, jmsMessage, session);
		}
		catch (MessagingException ex) {
			throw new ListenerExecutionFailedException(
					createMessagingErrorMessage("Listener method could not be invoked with incoming message"), ex);
		}
		catch (Exception ex) {
			throw new ListenerExecutionFailedException("Listener method '" +
					handlerMethod.getMethod().toGenericString() + "' threw exception", ex);
		}
	}

	private String createMessagingErrorMessage(String description) {
		InvocableHandlerMethod handlerMethod = getHandlerMethod();
		StringBuilder sb = new StringBuilder(description).append("\n")
				.append("Endpoint handler details:\n")
				.append("Method [").append(handlerMethod.getMethod()).append("]\n")
				.append("Bean [").append(handlerMethod.getBean()).append("]\n");
		return sb.toString();
	}

}
