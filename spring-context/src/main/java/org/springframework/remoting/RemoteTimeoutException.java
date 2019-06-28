

package org.springframework.remoting;

/**
 * RemoteAccessException subclass to be thrown when the execution
 * of the target method did not complete before a configurable
 * timeout, for example when a reply message was not received.
 * @author Stephane Nicoll
 * @since 4.2
 */
@SuppressWarnings("serial")
public class RemoteTimeoutException extends RemoteAccessException {

	/**
	 * Constructor for RemoteTimeoutException.
	 * @param msg the detail message
	 */
	public RemoteTimeoutException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for RemoteTimeoutException.
	 * @param msg the detail message
	 * @param cause the root cause from the remoting API in use
	 */
	public RemoteTimeoutException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
