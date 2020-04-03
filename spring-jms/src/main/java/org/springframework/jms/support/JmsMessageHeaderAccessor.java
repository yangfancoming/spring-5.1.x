

package org.springframework.jms.support;

import java.util.List;
import java.util.Map;
import javax.jms.Destination;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;

/**
 * A {@link org.springframework.messaging.support.MessageHeaderAccessor}
 * implementation giving access to JMS-specific headers.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class JmsMessageHeaderAccessor extends NativeMessageHeaderAccessor {

	protected JmsMessageHeaderAccessor(Map<String, List<String>> nativeHeaders) {
		super(nativeHeaders);
	}

	protected JmsMessageHeaderAccessor(Message<?> message) {
		super(message);
	}


	/**
	 * Return the {@link JmsHeaders#CORRELATION_ID correlationId}.
	 * @see JmsHeaders#CORRELATION_ID
	 */
	@Nullable
	public String getCorrelationId() {
		return (String) getHeader(JmsHeaders.CORRELATION_ID);
	}

	/**
	 * Return the {@link JmsHeaders#DESTINATION destination}.
	 * @see JmsHeaders#DESTINATION
	 */
	@Nullable
	public Destination getDestination() {
		return (Destination) getHeader(JmsHeaders.DESTINATION);
	}

	/**
	 * Return the {@link JmsHeaders#DELIVERY_MODE delivery mode}.
	 * @see JmsHeaders#DELIVERY_MODE
	 */
	@Nullable
	public Integer getDeliveryMode() {
		return (Integer) getHeader(JmsHeaders.DELIVERY_MODE);
	}

	/**
	 * Return the message {@link JmsHeaders#EXPIRATION expiration}.
	 * @see JmsHeaders#EXPIRATION
	 */
	@Nullable
	public Long getExpiration() {
		return (Long) getHeader(JmsHeaders.EXPIRATION);
	}

	/**
	 * Return the {@link JmsHeaders#MESSAGE_ID message id}.
	 * @see JmsHeaders#MESSAGE_ID
	 */
	@Nullable
	public String getMessageId() {
		return (String) getHeader(JmsHeaders.MESSAGE_ID);
	}

	/**
	 * Return the {@link JmsHeaders#PRIORITY priority}.
	 * @see JmsHeaders#PRIORITY
	 */
	@Nullable
	public Integer getPriority() {
		return (Integer) getHeader(JmsHeaders.PRIORITY);
	}

	/**
	 * Return the {@link JmsHeaders#REPLY_TO reply to}.
	 * @see JmsHeaders#REPLY_TO
	 */
	@Nullable
	public Destination getReplyTo() {
		return (Destination) getHeader(JmsHeaders.REPLY_TO);
	}

	/**
	 * Return the {@link JmsHeaders#REDELIVERED redelivered} flag.
	 * @see JmsHeaders#REDELIVERED
	 */
	@Nullable
	public Boolean getRedelivered() {
		return (Boolean) getHeader(JmsHeaders.REDELIVERED);
	}

	/**
	 * Return the {@link JmsHeaders#TYPE type}.
	 * @see JmsHeaders#TYPE
	 */
	@Nullable
	public String getType() {
		return (String) getHeader(JmsHeaders.TYPE);
	}

	/**
	 * Return the {@link JmsHeaders#TIMESTAMP timestamp}.
	 * @see JmsHeaders#TIMESTAMP
	 */
	@Override
	@Nullable
	public Long getTimestamp() {
		return (Long) getHeader(JmsHeaders.TIMESTAMP);
	}


	// Static factory method

	/**
	 * Create a {@link JmsMessageHeaderAccessor} from the headers of an existing message.
	 */
	public static JmsMessageHeaderAccessor wrap(Message<?> message) {
		return new JmsMessageHeaderAccessor(message);
	}

}
