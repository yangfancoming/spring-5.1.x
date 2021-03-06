

package org.springframework.messaging.core;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * Operations for sending messages to a destination.
 *
 * @author Mark Fisher
 *
 * @since 4.0
 * @param <D> the destination type
 */
public interface MessageSendingOperations<D> {

	/**
	 * Send a message to a default destination.
	 * @param message the message to send
	 */
	void send(Message<?> message) throws MessagingException;

	/**
	 * Send a message to the given destination.
	 * @param destination the target destination
	 * @param message the message to send
	 */
	void send(D destination, Message<?> message) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message and send it to a default destination.
	 * @param payload the Object to use as payload
	 */
	void convertAndSend(Object payload) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message and send it to the given destination.
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 */
	void convertAndSend(D destination, Object payload) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers and send it to
	 * the given destination.
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 * @param headers headers for the message to send
	 */
	void convertAndSend(D destination, Object payload, Map<String, Object> headers) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message, apply the given post processor, and send
	 * the resulting message to a default destination.
	 * @param payload the Object to use as payload
	 * @param postProcessor the post processor to apply to the message
	 */
	void convertAndSend(Object payload, @Nullable MessagePostProcessor postProcessor) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message, apply the given post processor, and send
	 * the resulting message to the given destination.
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 * @param postProcessor the post processor to apply to the message
	 */
	void convertAndSend(D destination, Object payload, MessagePostProcessor postProcessor) throws MessagingException;

	/**
	 * Convert the given Object to serialized form, possibly using a
	 * {@link org.springframework.messaging.converter.MessageConverter},
	 * wrap it as a message with the given headers, apply the given post processor,
	 * and send the resulting message to the given destination.
	 * @param destination the target destination
	 * @param payload the Object to use as payload
	 * @param headers headers for the message to send
	 * @param postProcessor the post processor to apply to the message
	 */
	void convertAndSend(D destination, Object payload, @Nullable Map<String, Object> headers,
			@Nullable MessagePostProcessor postProcessor) throws MessagingException;

}
