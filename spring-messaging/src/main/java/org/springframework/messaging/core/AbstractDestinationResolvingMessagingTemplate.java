

package org.springframework.messaging.core;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * An extension of {@link AbstractMessagingTemplate} that adds operations for sending
 * messages to a resolvable destination name. Supports destination resolving as defined by
 * the following interfaces:
 * <ul>
 * <li>{@link DestinationResolvingMessageSendingOperations}</li>
 * <li>{@link DestinationResolvingMessageReceivingOperations}</li>
 * <li>{@link DestinationResolvingMessageRequestReplyOperations}</li>
 * </ul>
 *
 * @author Mark Fisher
 * @author Rossen Stoyanchev
 * @since 4.0
 * @param <D> the destination type
 */
public abstract class AbstractDestinationResolvingMessagingTemplate<D> extends AbstractMessagingTemplate<D>
		implements DestinationResolvingMessageSendingOperations<D>,
		DestinationResolvingMessageReceivingOperations<D>,
		DestinationResolvingMessageRequestReplyOperations<D> {

	@Nullable
	private DestinationResolver<D> destinationResolver;


	/**
	 * Configure the {@link DestinationResolver} to use to resolve String destination
	 * names into actual destinations of type {@code <D>}.
	 * <p>This field does not have a default setting. If not configured, methods that
	 * require resolving a destination name will raise an {@link IllegalArgumentException}.
	 * @param destinationResolver the destination resolver to use
	 */
	public void setDestinationResolver(@Nullable DestinationResolver<D> destinationResolver) {
		this.destinationResolver = destinationResolver;
	}

	/**
	 * Return the configured destination resolver.
	 */
	@Nullable
	public DestinationResolver<D> getDestinationResolver() {
		return this.destinationResolver;
	}


	@Override
	public void send(String destinationName, Message<?> message) {
		D destination = resolveDestination(destinationName);
		doSend(destination, message);
	}

	protected final D resolveDestination(String destinationName) {

		Assert.state(this.destinationResolver != null, "DestinationResolver is required to resolve destination names");
		return this.destinationResolver.resolveDestination(destinationName);
	}

	@Override
	public <T> void convertAndSend(String destinationName, T payload) {
		convertAndSend(destinationName, payload, null, null);
	}

	@Override
	public <T> void convertAndSend(String destinationName, T payload, @Nullable Map<String, Object> headers) {
		convertAndSend(destinationName, payload, headers, null);
	}

	@Override
	public <T> void convertAndSend(String destinationName, T payload, @Nullable MessagePostProcessor postProcessor) {
		convertAndSend(destinationName, payload, null, postProcessor);
	}

	@Override
	public <T> void convertAndSend(String destinationName, T payload,
			@Nullable Map<String, Object> headers, @Nullable MessagePostProcessor postProcessor) {

		D destination = resolveDestination(destinationName);
		super.convertAndSend(destination, payload, headers, postProcessor);
	}

	@Override
	@Nullable
	public Message<?> receive(String destinationName) {
		D destination = resolveDestination(destinationName);
		return super.receive(destination);
	}

	@Override
	@Nullable
	public <T> T receiveAndConvert(String destinationName, Class<T> targetClass) {
		D destination = resolveDestination(destinationName);
		return super.receiveAndConvert(destination, targetClass);
	}

	@Override
	@Nullable
	public Message<?> sendAndReceive(String destinationName, Message<?> requestMessage) {
		D destination = resolveDestination(destinationName);
		return super.sendAndReceive(destination, requestMessage);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(String destinationName, Object request, Class<T> targetClass) {
		D destination = resolveDestination(destinationName);
		return super.convertSendAndReceive(destination, request, targetClass);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(String destinationName, Object request,
			@Nullable Map<String, Object> headers, Class<T> targetClass) {

		D destination = resolveDestination(destinationName);
		return super.convertSendAndReceive(destination, request, headers, targetClass);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(String destinationName, Object request, Class<T> targetClass,
			@Nullable MessagePostProcessor postProcessor) {

		D destination = resolveDestination(destinationName);
		return super.convertSendAndReceive(destination, request, targetClass, postProcessor);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(String destinationName, Object request,
			@Nullable Map<String, Object> headers, Class<T> targetClass,
			@Nullable MessagePostProcessor postProcessor) {

		D destination = resolveDestination(destinationName);
		return super.convertSendAndReceive(destination, request, headers, targetClass, postProcessor);
	}

}
