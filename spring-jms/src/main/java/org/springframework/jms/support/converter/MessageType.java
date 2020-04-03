

package org.springframework.jms.support.converter;

/**
 * Constants that indicate a target message type to convert to: a
 * {@link javax.jms.TextMessage}, a {@link javax.jms.BytesMessage},
 * a {@link javax.jms.MapMessage} or an {@link javax.jms.ObjectMessage}.
 *

 * @since 3.0
 * @see MarshallingMessageConverter#setTargetType
 */
public enum MessageType {

	/**
	 * A {@link javax.jms.TextMessage}.
	 */
	TEXT,

	/**
	 * A {@link javax.jms.BytesMessage}.
	 */
	BYTES,

	/**
	 * A {@link javax.jms.MapMessage}.
	 */
	MAP,

	/**
	 * A {@link javax.jms.ObjectMessage}.
	 */
	OBJECT

}
