

package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;

/**
 * Allows customizing the request before its body is read and converted into an
 * Object and also allows for processing of the resulting Object before it is
 * passed into a controller method as an {@code @RequestBody} or an
 * {@code HttpEntity} method argument.
 *
 * Implementations of this contract may be registered directly with the
 * {@code RequestMappingHandlerAdapter} or more likely annotated with
 * {@code @ControllerAdvice} in which case they are auto-detected.
 *
 *
 * @since 4.2
 */
public interface RequestBodyAdvice {

	/**
	 * Invoked first to determine if this interceptor applies.
	 * @param methodParameter the method parameter
	 * @param targetType the target type, not necessarily the same as the method
	 * parameter type, e.g. for {@code HttpEntity<String>}.
	 * @param converterType the selected converter type
	 * @return whether this interceptor should be invoked or not
	 */
	boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType);

	/**
	 * Invoked second before the request body is read and converted.
	 * @param inputMessage the request
	 * @param parameter the target method parameter
	 * @param targetType the target type, not necessarily the same as the method
	 * parameter type, e.g. for {@code HttpEntity<String>}.
	 * @param converterType the converter used to deserialize the body
	 * @return the input request or a new instance, never {@code null}
	 */
	HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException;

	/**
	 * Invoked third (and last) after the request body is converted to an Object.
	 * @param body set to the converter Object before the first advice is called
	 * @param inputMessage the request
	 * @param parameter the target method parameter
	 * @param targetType the target type, not necessarily the same as the method
	 * parameter type, e.g. for {@code HttpEntity<String>}.
	 * @param converterType the converter used to deserialize the body
	 * @return the same body or a new instance
	 */
	Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType);

	/**
	 * Invoked second (and last) if the body is empty.
	 * @param body usually set to {@code null} before the first advice is called
	 * @param inputMessage the request
	 * @param parameter the method parameter
	 * @param targetType the target type, not necessarily the same as the method
	 * parameter type, e.g. for {@code HttpEntity<String>}.
	 * @param converterType the selected converter type
	 * @return the value to use or {@code null} which may then raise an
	 * {@code HttpMessageNotReadableException} if the argument is required.
	 */
	@Nullable
	Object handleEmptyBody(@Nullable Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType);


}
