

package org.springframework.jms;

/**
 * JmsException to be thrown when no other matching subclass found.
 *

 * @since 1.1
 */
@SuppressWarnings("serial")
public class UncategorizedJmsException extends JmsException {

	/**
	 * Constructor that takes a message.
	 * @param msg the detail message
	 */
	public UncategorizedJmsException(String msg) {
		super(msg);
	}

	/**
	 * Constructor that takes a message and a root cause.
	 * @param msg the detail message
	 * @param cause the cause of the exception. This argument is generally
	 * expected to be a proper subclass of {@link javax.jms.JMSException},
	 * but can also be a JNDI NamingException or the like.
	 */
	public UncategorizedJmsException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Constructor that takes a root cause only.
	 * @param cause the cause of the exception. This argument is generally
	 * expected to be a proper subclass of {@link javax.jms.JMSException},
	 * but can also be a JNDI NamingException or the like.
	 */
	public UncategorizedJmsException(Throwable cause) {
		super("Uncategorized exception occurred during JMS processing", cause);
	}

}
