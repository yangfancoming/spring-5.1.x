

package org.springframework.messaging.handler.annotation.support;

import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * {@code HandlerMethodArgumentResolver} for {@link Message} method arguments.
 * Validates that the generic type of the payload matches to the message value
 * or otherwise applies {@link MessageConverter} to convert to the expected
 * payload type.
 *
 * @author Rossen Stoyanchev
 * @author Stephane Nicoll

 * @since 4.0
 */
public class MessageMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private final MessageConverter converter;


	/**
	 * Create a default resolver instance without message conversion.
	 */
	public MessageMethodArgumentResolver() {
		this(null);
	}

	/**
	 * Create a resolver instance with the given {@link MessageConverter}.
	 * @param converter the MessageConverter to use (may be {@code null})
	 * @since 4.3
	 */
	public MessageMethodArgumentResolver(@Nullable MessageConverter converter) {
		this.converter = converter;
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Message.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
		Class<?> targetMessageType = parameter.getParameterType();
		Class<?> targetPayloadType = getPayloadType(parameter);

		if (!targetMessageType.isAssignableFrom(message.getClass())) {
			throw new MethodArgumentTypeMismatchException(message, parameter, "Actual message type '" +
					ClassUtils.getDescriptiveType(message) + "' does not match expected type '" +
					ClassUtils.getQualifiedName(targetMessageType) + "'");
		}

		Object payload = message.getPayload();
		if (targetPayloadType.isInstance(payload)) {
			return message;
		}

		if (isEmptyPayload(payload)) {
			throw new MessageConversionException(message, "Cannot convert from actual payload type '" +
					ClassUtils.getDescriptiveType(payload) + "' to expected payload type '" +
					ClassUtils.getQualifiedName(targetPayloadType) + "' when payload is empty");
		}

		payload = convertPayload(message, parameter, targetPayloadType);
		return MessageBuilder.createMessage(payload, message.getHeaders());
	}

	private Class<?> getPayloadType(MethodParameter parameter) {
		Type genericParamType = parameter.getGenericParameterType();
		ResolvableType resolvableType = ResolvableType.forType(genericParamType).as(Message.class);
		return resolvableType.getGeneric().toClass();
	}

	/**
	 * Check if the given {@code payload} is empty.
	 * @param payload the payload to check (can be {@code null})
	 */
	protected boolean isEmptyPayload(@Nullable Object payload) {
		if (payload == null) {
			return true;
		}
		else if (payload instanceof byte[]) {
			return ((byte[]) payload).length == 0;
		}
		else if (payload instanceof String) {
			return !StringUtils.hasText((String) payload);
		}
		else {
			return false;
		}
	}

	private Object convertPayload(Message<?> message, MethodParameter parameter, Class<?> targetPayloadType) {
		Object result = null;
		if (this.converter instanceof SmartMessageConverter) {
			SmartMessageConverter smartConverter = (SmartMessageConverter) this.converter;
			result = smartConverter.fromMessage(message, targetPayloadType, parameter);
		}
		else if (this.converter != null) {
			result = this.converter.fromMessage(message, targetPayloadType);
		}

		if (result == null) {
			throw new MessageConversionException(message, "No converter found from actual payload type '" +
					ClassUtils.getDescriptiveType(message.getPayload()) + "' to expected payload type '" +
					ClassUtils.getQualifiedName(targetPayloadType) + "'");
		}
		return result;
	}

}
