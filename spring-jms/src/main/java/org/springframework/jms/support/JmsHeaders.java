

package org.springframework.jms.support;

/**
 * Pre-defined names and prefixes to be used for setting and/or retrieving
 * JMS attributes from/to generic message headers.
 *
 * @author Mark Fisher
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface JmsHeaders {

	/**
	 * Prefix used for JMS API related headers in order to distinguish from
	 * user-defined headers and other internal headers (e.g. correlationId).
	 * @see SimpleJmsHeaderMapper
	 */
	String PREFIX = "jms_";

	/**
	 * Correlation ID for the message. This may be the {@link #MESSAGE_ID} of
	 * the message that this message replies to. It may also be an
	 * application-specific identifier.
	 * @see javax.jms.Message#getJMSCorrelationID()
	 */
	String CORRELATION_ID = PREFIX + "correlationId";

	/**
	 * Name of the destination (topic or queue) of the message.
	 * xmlBeanDefinitionReaderRead-only value.
	 * @see javax.jms.Message#getJMSDestination()
	 * @see javax.jms.Destination
	 * @see javax.jms.Queue
	 * @see javax.jms.Topic
	 */
	String DESTINATION = PREFIX + "destination";

	/**
	 * Distribution mode.
	 * xmlBeanDefinitionReaderRead-only value.
	 * @see javax.jms.Message#getJMSDeliveryMode()
	 * @see javax.jms.DeliveryMode
	 */
	String DELIVERY_MODE = PREFIX + "deliveryMode";

	/**
	 * Message expiration date and time.
	 * xmlBeanDefinitionReaderRead-only value.
	 * @see javax.jms.Message#getJMSExpiration()
	 */
	String EXPIRATION = PREFIX + "expiration";

	/**
	 * Unique identifier for a message.
	 * xmlBeanDefinitionReaderRead-only value.
	 * @see javax.jms.Message#getJMSMessageID()
	 */
	String MESSAGE_ID = PREFIX + "messageId";

	/**
	 * The message priority level.
	 * xmlBeanDefinitionReaderRead-only value.
	 * @see javax.jms.Message#getJMSPriority()
	 */
	String PRIORITY = PREFIX + "priority";

	/**
	 * Name of the destination (topic or queue) the message replies should
	 * be sent to.
	 * @see javax.jms.Message#getJMSReplyTo()
	 */
	String REPLY_TO = PREFIX + "replyTo";

	/**
	 * Specify if the message was resent. This occurs when a message
	 * consumer fails to acknowledge the message reception.
	 * xmlBeanDefinitionReaderRead-only value.
	 * @see javax.jms.Message#getJMSRedelivered()
	 */
	String REDELIVERED = PREFIX + "redelivered";

	/**
	 * Message type label. This type is a string value describing the message
	 * in a functional manner.
	 * @see javax.jms.Message#getJMSType()
	 */
	String TYPE = PREFIX + "type";

	/**
	 * Date and time of the message sending operation.
	 * xmlBeanDefinitionReaderRead-only value.
	 * @see javax.jms.Message#getJMSTimestamp()
	 */
	String TIMESTAMP = PREFIX + "timestamp";

}
