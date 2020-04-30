

package org.springframework.messaging.simp.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.util.Assert;

/**
 * Base class for message broker registration classes.
 *
 *
 * @since 4.0
 */
public abstract class AbstractBrokerRegistration {

	private final SubscribableChannel clientInboundChannel;

	private final MessageChannel clientOutboundChannel;

	private final List<String> destinationPrefixes;


	public AbstractBrokerRegistration(SubscribableChannel clientInboundChannel,
			MessageChannel clientOutboundChannel, @Nullable String[] destinationPrefixes) {

		Assert.notNull(clientOutboundChannel, "'clientInboundChannel' must not be null");
		Assert.notNull(clientOutboundChannel, "'clientOutboundChannel' must not be null");

		this.clientInboundChannel = clientInboundChannel;
		this.clientOutboundChannel = clientOutboundChannel;

		this.destinationPrefixes = (destinationPrefixes != null ?
				Arrays.asList(destinationPrefixes) : Collections.emptyList());
	}


	protected SubscribableChannel getClientInboundChannel() {
		return this.clientInboundChannel;
	}

	protected MessageChannel getClientOutboundChannel() {
		return this.clientOutboundChannel;
	}

	protected Collection<String> getDestinationPrefixes() {
		return this.destinationPrefixes;
	}


	protected abstract AbstractBrokerMessageHandler getMessageHandler(SubscribableChannel brokerChannel);

}
