

package org.springframework.messaging.core;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

/**
 * An extension of {@link AbstractMessageReceivingTemplate} that adds support for
 * request-reply style operations as defined by {@link MessageRequestReplyOperations}.
 *
 * @author Mark Fisher
 *
 * @author Stephane Nicoll
 * @since 4.0
 * @param <D> the destination type
 */
public abstract class AbstractMessagingTemplate<D> extends AbstractMessageReceivingTemplate<D>
		implements MessageRequestReplyOperations<D> {

	@Override
	@Nullable
	public Message<?> sendAndReceive(Message<?> requestMessage) {
		return sendAndReceive(getRequiredDefaultDestination(), requestMessage);
	}

	@Override
	@Nullable
	public Message<?> sendAndReceive(D destination, Message<?> requestMessage) {
		return doSendAndReceive(destination, requestMessage);
	}

	@Nullable
	protected abstract Message<?> doSendAndReceive(D destination, Message<?> requestMessage);


	@Override
	@Nullable
	public <T> T convertSendAndReceive(Object request, Class<T> targetClass) {
		return convertSendAndReceive(getRequiredDefaultDestination(), request, targetClass);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(D destination, Object request, Class<T> targetClass) {
		return convertSendAndReceive(destination, request, null, targetClass);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(
			D destination, Object request, @Nullable Map<String, Object> headers, Class<T> targetClass) {

		return convertSendAndReceive(destination, request, headers, targetClass, null);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(
			Object request, Class<T> targetClass, @Nullable MessagePostProcessor postProcessor) {

		return convertSendAndReceive(getRequiredDefaultDestination(), request, targetClass, postProcessor);
	}

	@Override
	@Nullable
	public <T> T convertSendAndReceive(D destination, Object request, Class<T> targetClass,
			@Nullable MessagePostProcessor postProcessor) {

		return convertSendAndReceive(destination, request, null, targetClass, postProcessor);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T convertSendAndReceive(D destination, Object request, @Nullable Map<String, Object> headers,
			Class<T> targetClass, @Nullable MessagePostProcessor postProcessor) {

		Message<?> requestMessage = doConvert(request, headers, postProcessor);
		Message<?> replyMessage = sendAndReceive(destination, requestMessage);
		return (replyMessage != null ? (T) getMessageConverter().fromMessage(replyMessage, targetClass) : null);
	}

}
