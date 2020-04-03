

package org.springframework.jms.config;

import javax.jms.MessageListener;

import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * A {@link JmsListenerEndpoint} simply providing the {@link MessageListener} to
 * invoke to process an incoming message for this endpoint.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class SimpleJmsListenerEndpoint extends AbstractJmsListenerEndpoint {

	@Nullable
	private MessageListener messageListener;


	/**
	 * Set the {@link MessageListener} to invoke when a message matching
	 * the endpoint is received.
	 */
	public void setMessageListener(@Nullable MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	/**
	 * Return the {@link MessageListener} to invoke when a message matching
	 * the endpoint is received.
	 */
	@Nullable
	public MessageListener getMessageListener() {
		return this.messageListener;
	}


	@Override
	protected MessageListener createMessageListener(MessageListenerContainer container) {
		MessageListener listener = getMessageListener();
		Assert.state(listener != null, "No MessageListener set");
		return listener;
	}

	@Override
	protected StringBuilder getEndpointDescription() {
		return super.getEndpointDescription()
				.append(" | messageListener='").append(this.messageListener).append("'");
	}

}
