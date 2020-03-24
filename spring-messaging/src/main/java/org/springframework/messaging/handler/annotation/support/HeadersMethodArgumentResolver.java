

package org.springframework.messaging.handler.annotation.support;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.ReflectionUtils;

/**
 * {@link HandlerMethodArgumentResolver} for header method parameters. Resolves the
 * following method parameters:
 * <ul>
 * <li>Parameters assignable to {@link Map} annotated with {@link Headers @Headers}
 * <li>Parameters of type {@link MessageHeaders}
 * <li>Parameters assignable to {@link MessageHeaderAccessor}
 * </ul>
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class HeadersMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return ((parameter.hasParameterAnnotation(Headers.class) && Map.class.isAssignableFrom(paramType)) ||
				MessageHeaders.class == paramType || MessageHeaderAccessor.class.isAssignableFrom(paramType));
	}

	@Override
	@Nullable
	public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
		Class<?> paramType = parameter.getParameterType();
		if (Map.class.isAssignableFrom(paramType)) {
			return message.getHeaders();
		}
		else if (MessageHeaderAccessor.class == paramType) {
			MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
			return (accessor != null ? accessor : new MessageHeaderAccessor(message));
		}
		else if (MessageHeaderAccessor.class.isAssignableFrom(paramType)) {
			MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
			if (accessor != null && paramType.isAssignableFrom(accessor.getClass())) {
				return accessor;
			}
			else {
				Method method = ReflectionUtils.findMethod(paramType, "wrap", Message.class);
				if (method == null) {
					throw new IllegalStateException(
							"Cannot create accessor of type " + paramType + " for message " + message);
				}
				return ReflectionUtils.invokeMethod(method, null, message);
			}
		}
		else {
			throw new IllegalStateException(
					"Unexpected method parameter type " + paramType + "in method " + parameter.getMethod() + ". "
					+ "@Headers method arguments must be assignable to java.util.Map.");
		}
	}

}
